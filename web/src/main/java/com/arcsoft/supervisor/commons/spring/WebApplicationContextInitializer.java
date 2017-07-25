package com.arcsoft.supervisor.commons.spring;

import com.arcsoft.supervisor.utils.app.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author zw.
 */
public class WebApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger LOG = LoggerFactory.getLogger(WebApplicationContextInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            Environment.initialize();
        } catch (URISyntaxException | IOException e) {
            LOG.error("Failed to load environment configuration", e);
        }
        if (Environment.getExpireChecker().isExpired()) {
            LOG.error("Shutdown system cause by evaluate date expired");
            System.exit(0);
        }
    }
}
