package com.arcsoft.supervisor;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author zw.
 */
@WebAppConfiguration
public abstract class BaseWebTestSupport extends BaseSpringTestSupport {

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
    }

}
