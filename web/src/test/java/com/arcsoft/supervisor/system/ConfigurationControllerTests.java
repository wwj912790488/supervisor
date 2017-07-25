package com.arcsoft.supervisor.system;

import com.arcsoft.supervisor.ProductionTestSupport;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author zw.
 */
public class ConfigurationControllerTests extends ProductionTestSupport {

    private String RTSP_JSON = "{\"publishUrl\" : \"127.0.0.1\", \"publishFolderPath\" : \"/usr/local/arcsoft\"}";
    private String UPDATE_JSON = "{\"id\" : 1, \"publishUrl\" : \"127.0.0.2\", \"publishFolderPath\" : \"/usr/local/arcsoft\"}";

    @Test
    public void testRtsp() throws Exception {
        mockMvc.perform(post("/cfg/rtsp").contentType(MediaType.APPLICATION_JSON).content(RTSP_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code", is(0)));

        mockMvc.perform(post("/cfg/rtsp").contentType(MediaType.APPLICATION_JSON).content(UPDATE_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code", is(0)));

        mockMvc.perform(get("/cfg/rtsp"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.publishUrl", is("127.0.0.2")));
    }
}
