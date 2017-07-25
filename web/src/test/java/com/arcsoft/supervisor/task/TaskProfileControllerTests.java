package com.arcsoft.supervisor.task;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.service.profile.TaskProfileService;
import com.arcsoft.supervisor.web.profile.ProfileController;
import com.google.common.base.Joiner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import static com.arcsoft.supervisor.commons.SupervisorDefs.Constants.PAGER;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for controller of task profile.
 *
 * @author zw.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskProfileControllerTests extends ProductionTestSupport {

    @Autowired
    private TaskProfileService taskProfileService;

    @SuppressWarnings("all")
    @Test
    public void testATaskProfile() throws Exception {
        mockMvc.perform(get("/profile/task"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.VIEW_TASK_INDEX))
                .andExpect(model().attribute(PAGER, hasProperty("totalPages", is(1))))
                .andExpect(model().attribute(PAGER, hasProperty("size", is(10))))
                .andExpect(model().attribute(PAGER, hasProperty("totalElements", is(3L))))
                .andExpect(model().attribute(PAGER, hasProperty("content", hasItems(
                        allOf(
                                hasProperty("id", is(4)),
                                hasProperty("name", is("task-profile-1"))
                        ),
                        allOf(
                                hasProperty("id", is(5)),
                                hasProperty("name", is("task-profile-2"))
                        ),
                        allOf(
                                hasProperty("id", is(6)),
                                hasProperty("name", is("task-profile-3"))
                        )
                ))));
    }

    @Test
    public void testBGetTaskProfile() throws Exception {
        mockMvc.perform(get("/profile/task/{id}", 4))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.taskname", is("task-profile-1")));
    }

    @Test
    public void testCEditTaskProfile() throws Exception {
        mockMvc.perform(get("/profile/task/new/0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("op", is("new")))
                .andExpect(model().attribute("profile", nullValue()));

        mockMvc.perform(get("/profile/task/edit/{id}", 4))
                .andExpect(status().isOk())
                .andExpect(model().attribute("op", is("edit")))
                .andExpect(model().attribute("profile", notNullValue()));

        mockMvc.perform(get("/profile/task/copy/{id}", 4))
                .andExpect(status().isOk())
                .andExpect(model().attribute("op", is("copy")))
                .andExpect(model().attribute("profile", notNullValue()));
    }

    @Test
    public void testCFindByLayout() throws Exception {
        mockMvc.perform(get("/profile/task/layout/{row}/{column}", 3, 3))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(4)))
                .andExpect(jsonPath("$[1].id", is(5)))
                .andExpect(jsonPath("$[2].id", is(6)));
    }

    @Test
    public void testDSaveTaskProfile() throws Exception {
        TaskProfileDto taskProfileDto = taskProfileService.find(4);
        taskProfileDto.setId(null);
        taskProfileDto.setDescription("desc-4");
        taskProfileDto.setName("task-profile-4");
        String json = JsonMapper.getMapper().writeValueAsString(taskProfileDto);
        mockMvc.perform(post("/profile/task").contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        TaskProfileDto savedTaskProfile = taskProfileService.find(7);
        assertEquals(taskProfileDto.getName(), savedTaskProfile.getName());
        assertEquals(taskProfileDto.getDescription(), savedTaskProfile.getDescription());

    }

    @Test
    public void testEUpdateTaskProfile() throws Exception {
        TaskProfileDto taskProfileDto = taskProfileService.find(4);
        taskProfileDto.setName("task-profile-updated");
        String json = JsonMapper.getMapper().writeValueAsString(taskProfileDto);
        mockMvc.perform(
                put("/profile/task/{id}", taskProfileDto.getId())
                        .contentType("application/json;charset=UTF-8")
                        .content(json)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        TaskProfileDto updatedTaskProfile = taskProfileService.find(taskProfileDto.getId());
        assertEquals(taskProfileDto.getName(), updatedTaskProfile.getName());

    }


    @Test
    public void testZDeleteTaskProfiles() throws Exception {
        String[] profileIds = {"4", "5", "6"};
        mockMvc.perform(delete("/profile/task/{ids}", Joiner.on(",").join(profileIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code", is(0)));
    }

}
