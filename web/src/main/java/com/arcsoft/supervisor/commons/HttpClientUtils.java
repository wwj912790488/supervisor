package com.arcsoft.supervisor.commons;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Utility class for http client.
 *
 * @author zw.
 */
public class HttpClientUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);

    private static final int TIMEOUT = 5000;
    private static final int SocketTimeout = 5000;
    private static final int ConnectionRequestTimeout = 5000;

    public static String doPostJSON(String uri, Object object) throws IOException {
        byte[] objBytes = JsonMapper.getMapper().writeValueAsBytes(object);
        return doPostJSON(uri, objBytes);
    }

    public static String doPostJSON(String uri, byte[] data) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost req = new HttpPost(uri);
        req.setConfig(RequestConfig.custom().setSocketTimeout(SocketTimeout).setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(ConnectionRequestTimeout).setStaleConnectionCheckEnabled(true).build());
        req.setEntity(new ByteArrayEntity(data, ContentType.APPLICATION_JSON));
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
                LOG.warn("Failed to close the http client with url {}", uri);
            }
        }
    }

    public static String doDeleteJSON(String uri) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete req = new HttpDelete(uri);
        req.setConfig(RequestConfig.custom().setSocketTimeout(SocketTimeout).setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(ConnectionRequestTimeout).setStaleConnectionCheckEnabled(true).build());
        try {
            return client.execute(req, new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws IOException {
                    HttpEntity entity = response.getEntity();
                    return EntityUtils.toString(entity, Charset.forName("UTF-8"));
                }
            });
        } finally {
            try {
                client.close();
            } catch (IOException e) {

            }
        }

    }

    public static String doGetJSON(String uri) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet req = new HttpGet(uri);
        req.setConfig(RequestConfig.custom().setConnectTimeout(TIMEOUT).build());
        try {
            return client.execute(req, new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    HttpEntity entity = response.getEntity();
                    return EntityUtils.toString(entity, Charset.forName("UTF-8"));
                }
            });
        } finally {
            try {
                client.close();
            } catch (IOException e) {

            }
        }
    }

}
