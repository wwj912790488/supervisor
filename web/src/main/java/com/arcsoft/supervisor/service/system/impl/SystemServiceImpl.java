package com.arcsoft.supervisor.service.system.impl;


import com.arcsoft.supervisor.model.domain.system.SystemSettings;
import com.arcsoft.supervisor.model.dto.graphic.SdiOutputWebBean;
import com.arcsoft.supervisor.repository.system.SystemRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.system.SystemContextListener;
import com.arcsoft.supervisor.service.system.SystemService;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.arcsoft.supervisor.utils.NetworkHelper;
import com.arcsoft.supervisor.utils.StringHelper;
import com.arcsoft.supervisor.utils.SystemHelper;
import com.arcsoft.supervisor.utils.app.Environment;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * This class maintains the commander system context and system initialize,
 * So this is the first service to be initialized. If system is initialized,
 * then initialize the system context immediately; otherwise, waiting until
 * the system is initialized, then continue.
 *
 * @author fjli
 * @author zw
 */
@Service
public class SystemServiceImpl extends ServiceSupport implements SystemService, TransactionSupport {

    @Autowired
    private SystemRepository systemRepository;
    private boolean initialized;
    @Autowired
    private List<SystemContextListener> listeners;
    @Autowired
    private ContentDetectLogService contentDetectLogService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    //Injecting gpuLoadBalanceManager to ensure gpuLoadBalanceManager's postConstruct run before this postConstruct.
    @Autowired
    private GpuLoadBalanceManager gpuLoadBalanceManager;

    private static final ScheduledExecutorService expiredCheckerPool = Executors.newSingleThreadScheduledExecutor(
            NamedThreadFactory.create("SystemExpiredChecker")
    );
    private static final ScheduledExecutorService autoExecutor = Executors.newSingleThreadScheduledExecutor(
            NamedThreadFactory.create("AutoDaysDeleteContentLogs")
    );

    /**
     * Initialize system service.
     */
    @PostConstruct
    public void init() {
        logger.info("init system service.");
        SystemSettings settings = getSettings();
        startContext(settings);
        executeEightAtNightPerDay(systemRepository);
        expiredCheckerPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (Environment.getExpireChecker().isExpired()) {
                    logger.error("Shutdown system cause by evaluate date expire");
                    System.exit(0);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Destroy system service.
     */
    @PreDestroy
    public void destroy() {
        logger.info("destroy system service.");
        destroyContext();
        expiredCheckerPool.shutdownNow();
        autoExecutor.shutdownNow();
    }

    public static void executeEightAtNightPerDay( final SystemRepository systemRepository) {

        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay = getTimeMillis("11:30:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
        autoExecutor.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        Integer autoDays =systemRepository.getSettings().get(SystemSettings.ALERT_AUTO_DELETE_DAYS)==null?0:Integer.valueOf(systemRepository.getSettings().get(SystemSettings.ALERT_AUTO_DELETE_DAYS));
                        if(autoDays>0){
                              long endDay = 24 * 60 * 60 * 1000 * autoDays;
                            systemRepository.autoDeleteContentLogs(getTimeMillis("11:30:00") - endDay);
                        }
                    }
                },
                /*0,
                5,
                TimeUnit.SECONDS);*/
                initDelay,
                oneDay,
                TimeUnit.MINUTES);
    }

    private static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Start the context.
     *
     * @param settings
     */
    private synchronized void startContext(SystemSettings settings) {
        // if the system is initialized, destroy the previous one.
        if (initialized) {
            destroyContext();
        }

        // if the settings is valid, initialize the context.
        if (isValidSettings(settings)) {
            // Update system settings in cache.
            BeanUtils.copyProperties(settings, systemSettings);
            initContext();
        } else {
            logger.error("start system context failed: invalid system settings.");
        }
    }

    /**
     * Initialize the system context.
     */
    private synchronized void initContext() {
        logger.info("initialize system context.");
        // Finally, set the flag.
        initialized = true;
        for (SystemContextListener listener : listeners) {
            listener.contextInit();
        }
        logger.info("initialize system context finished.");
    }

    /**
     * Destroy the system context.
     */
    private synchronized void destroyContext() {
        logger.info("destroy system context.");
        // Finally, set the flag.
        initialized = false;
        for (SystemContextListener listener : listeners) {
            listener.contextDestory();
        }
        logger.info("destroy system context finished.");
    }

    /**
     * Check the settings is valid or not.
     */
    private boolean isValidSettings(SystemSettings settings) {
        return !(StringHelper.isBlank(settings.getClusterIp()) || settings.getClusterPort() == null
                || StringHelper.isBlank(settings.getBindAddr()) || settings.getClusterType() == null);
    }

    @Override
    public synchronized boolean isSystemInited() {
        return initialized;
    }

    @Override
    public SystemSettings getSettings() {
        HashMap<String, String> maps = systemRepository.getSettings();
        SystemSettings settings = new SystemSettings();
        settings.setClusterType(StringHelper.toInteger(maps.get(SystemSettings.CLUSTER_TYPE)));
        settings.setClusterIp(maps.get(SystemSettings.CLUSTER_IP));
        settings.setClusterPort(StringHelper.toInteger(maps.get(SystemSettings.CLUSTER_PORT)));
        String id = SystemHelper.os.getSystemUUID();
        String bindAddr = maps.get(SystemSettings.CLUSTER_BINDADDR + "_" + id);
        if (bindAddr == null) {
            String macAddr = maps.get(SystemSettings.CLUSTER_BINDADDR);
            if (macAddr != null)
                bindAddr = NetworkHelper.getHostAddressByMacAddr(macAddr);
        }
        settings.setAlertAutoDeleteDays("null".equals(maps.get(SystemSettings.ALERT_AUTO_DELETE_DAYS))?0:Integer.valueOf(maps.get(SystemSettings.ALERT_AUTO_DELETE_DAYS)));
        settings.setBindAddr(bindAddr);
        settings.setTimeToLive(StringHelper.toInteger(maps.get(SystemSettings.CLUSTER_TIME_TO_LIVE), 10));
        settings.setHeartbeatInterval(StringHelper.toInteger(maps.get(SystemSettings.CLUSTER_HEARTBEAT_INTERVAL), 100));
        settings.setHeartbeatTimeout(StringHelper.toInteger(maps.get(SystemSettings.CLUSTER_HEARTBEAT_TIMEOUT), 2000));
        return settings;
    }

    @Override
    public void saveSettings(SystemSettings settings) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put(SystemSettings.CLUSTER_IP, settings.getClusterIp());
        maps.put(SystemSettings.CLUSTER_PORT, String.valueOf(settings.getClusterPort()));
        String id = SystemHelper.os.getSystemUUID();
        maps.put(SystemSettings.CLUSTER_BINDADDR + "_" + id, settings.getBindAddr());
        maps.put(SystemSettings.CLUSTER_BINDADDR, null);
        maps.put(SystemSettings.CLUSTER_TYPE, String.valueOf(settings.getClusterType()));
        maps.put(SystemSettings.CLUSTER_TIME_TO_LIVE, String.valueOf(settings.getTimeToLive()));
        maps.put(SystemSettings.CLUSTER_HEARTBEAT_INTERVAL, String.valueOf(settings.getHeartbeatInterval()));
        maps.put(SystemSettings.CLUSTER_HEARTBEAT_TIMEOUT, String.valueOf(settings.getHeartbeatTimeout()));
        maps.put(SystemSettings.ALERT_AUTO_DELETE_DAYS, String.valueOf(settings.getAlertAutoDeleteDays()==null?0:settings.getAlertAutoDeleteDays()));
        systemRepository.saveSettings(maps);
        startContext(settings);
    }

}
