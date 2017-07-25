package com.arcsoft.supervisor.task;

import com.arcsoft.supervisor.ProductionTestSupport;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * @author zw.
 */
public class TaskControllerTests extends ProductionTestSupport {

    @Test
    public void testCheckProfile() throws Exception {
        mockMvc.perform(get("/task/checkProfile/{screenId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.r", is(true)));

        mockMvc.perform(get("/task/checkProfile/{screenId}", 2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.r", is(false)));
    }
}
