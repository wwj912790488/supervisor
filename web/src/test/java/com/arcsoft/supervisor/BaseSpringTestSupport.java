package com.arcsoft.supervisor;

import com.arcsoft.supervisor.commons.spring.WebApplicationContextInitializer;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A base class to do initialization of spring framework for test environment.
 *
 * @author zw.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
                "classpath:spring-application.xml",
                "classpath:spring-web.xml"
        },
        initializers = {
                WebApplicationContextInitializer.class
        }
)
public abstract class BaseSpringTestSupport {
}
