package com.arcsoft.supervisor.service.server.impl;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.WallPosition;
import com.arcsoft.supervisor.model.domain.server.AbstractOpsServer;
import com.arcsoft.supervisor.model.dto.rest.server.OpsServerChannel;
import com.arcsoft.supervisor.model.dto.rest.server.OpsServerRecognize;
import com.arcsoft.supervisor.model.dto.rest.setup.SetupPackageBean;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.graphic.WallService;
import com.arcsoft.supervisor.service.server.OpsServerOperator;
import com.arcsoft.supervisor.utils.NamedThreadFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zw.
 */
public abstract class AbstractOpsServerOperator<T extends AbstractOpsServer> extends ServiceSupport implements OpsServerOperator<T> {

    /**
     * The thread pool to execute the start and stop operation of ops.
     */
    private final ExecutorService pool;

    private static final String START_PATH = "/SetChannelInfo_app";

    private static final String SET_CLIENT_IDENTIFY_PATH = "/SetClientIdentify_app";

    private static final String STOP_PATH = "/StopPreview_app";
    
    private static final String DEPLOY_PACKAGE = "/DeployPackage_app";

    private static final int DEFAULT_CONNECT_TIMEOUT_MILLISECONDS = 10000;

    @Autowired
    WallService wallService;
    @Autowired
    private ScreenService screenService;

    public AbstractOpsServerOperator() {
        this.pool = Executors.newCachedThreadPool(NamedThreadFactory.create("OpsServerOperator"));
    }

    public ExecutorService getPool() {
        return pool;
    }

    public static String getStartPath() {
        return START_PATH;
    }

    public static String getSetClientIdentifyPath() {
        return SET_CLIENT_IDENTIFY_PATH;
    }

    public static String getStopPath() {
        return STOP_PATH;
    }

    @PreDestroy
    public void destroy() {
        this.pool.shutdownNow();
    }

    @Override
    public void start(final T server, final String url, final String source) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("Ready to send start request to ops [ip={}, url={}]", server.getIp(), url);
                try {
                    String result = doPost(START_PATH, server, JsonMapper.getMapper().writeValueAsBytes(OpsServerChannel.build(server, url, source)));
                    logger.info("Get result [{}] of start [ip={}] with data [url={}]", new Object[]{result, server.getIp(), url});
                } catch (IOException e) {
                    logger.error("Failed to do start operation on OpsServer [id=" + server.getId() + "], exception: " + e.getMessage());
                }

                //record the push url to screen
                updatePushUrltoScreen(server,url);
            }
        });
    }

    @Override
    public void recognize(T server, int number) {
        try {
            String result = doPost(SET_CLIENT_IDENTIFY_PATH, server, JsonMapper.getMapper().writeValueAsBytes(OpsServerRecognize.build(server, number)));
            logger.info("Get result [{}] from OpsServer [ip={}] with data [number={}]", new Object[]{result, server.getIp(), number});
        } catch (IOException e) {
            logger.error("Failed to set client identify on OpsServer [id=" + server.getId() + "], exception: " + e.getMessage());
        }

    }

    @Override
    public void stop(final T server) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Ready to send stop request to ops ip={}", server.getIp());
                    String result = doPost(STOP_PATH, server, new byte[0]);
                    logger.info("Get result [{}] of stop ip=[{}]", result, server.getIp());
                } catch (IOException e) {
                    logger.error("Failed to do stop operation on the OpsServer [id=" + server.getId() + "], exception: " + e.getMessage());
                }
                updatePushUrltoScreen(server,null);
            }
        });

    }
    
    @Override
    public void deployPackage(final T server, final String url, final String hash) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
            	logger.info("Ready to send deployPackage request to ops [ip={}], [url={}], [hash={}]", new Object[]{server.getIp(), url, hash});
                try {
                	String result = doPost(DEPLOY_PACKAGE, server, JsonMapper.getMapper().writeValueAsBytes(SetupPackageBean.build(url, hash)));
                	logger.info("Get result [{}] of start [ip={}] with data [url={}] [hash={}]", new Object[]{result, server.getIp(), url, hash});
                } catch (IOException e) {
                    logger.error("Failed to do deployPackage operation on the OpsServer [id=" + server.getId() + "], exception: " + e.getMessage());
                }
            }
        });

    }

    protected String doPost(String path, T server, byte[] data) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost req = new HttpPost(getHttpUrl(server.getIp(), server.getPort(), path));
        req.setConfig(RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLISECONDS).build());
        req.setEntity(new ByteArrayEntity(data));
        try {
            return client.execute(req, new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse httpResponse) throws IOException {
                    HttpEntity entity = httpResponse.getEntity();
                    return EntityUtils.toString(entity, Charset.forName("UTF-8"));
                }
            });
        } finally {

            try {
                client.close();
            } catch (IOException e) {
                logger.warn("Failed to close the http client of OpsServer [ip=" + server.getIp() + "]");
            }

        }
    }

    /**
     *
     * Returns ops server with given parameters.
     *
     * @param ip the ip
     * @param port the port
     * @return ops server
     */
    protected abstract T getOpsServer(String ip, String port);

    protected String doPost(String path, String ip, String port, byte[] data) throws IOException {

        return doPost(path, getOpsServer(ip, port), data);
    }

    private String getHttpUrl(String ip, String port, String path) {
        return "http://" + ip + ":" + (StringUtils.isBlank(port) ? "80" : port) + path;
    }

    private void updatePushUrltoScreen(T server, final String url){
        try {
            WallPosition position = wallService.getWallPositionWithOpsServerId(server.getId());
            if(position!=null){
                Screen screen = position.getScreen();
                screen.setPushUrl(url);

                screenService.save(screen);
            }
        }catch (Exception e){

        }
    }
}
