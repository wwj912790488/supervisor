package com.arcsoft.supervisor.task;

import com.arcsoft.supervisor.BaseWebTestSupport;
import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.model.domain.task.OutputProfile;
import com.arcsoft.supervisor.model.vo.task.profile.OutputProfileDto;
import com.arcsoft.supervisor.service.profile.ProfileService;
import com.arcsoft.supervisor.web.profile.ProfileController;
import com.google.common.base.Joiner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for controller of output profile.
 *
 * @author zw.
 */
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class OutputProfileControllerTests extends ProductionTestSupport {

    @Autowired
    private ProfileService<OutputProfile, OutputProfileDto> outputProfileService;

    @SuppressWarnings("all")
    @Test
    public void testAOutput() throws Exception {
        mockMvc.perform(get("/profile/output"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.VIEW_OUTPUT_INDEX))
                .andExpect(model().attribute("pager", hasProperty("totalPages", is(1))))
                .andExpect(model().attribute("pager", hasProperty("size", is(10))))
                .andExpect(model().attribute("pager", hasProperty("totalElements", is(3L))))
                .andExpect(model().attribute("pager", hasProperty("content", hasItems(
                        allOf(
                                hasProperty("id", is(1)),
                                hasProperty("name", is("output-profile-1")),
                                hasProperty("description", is("desc-1"))
                        ),
                        allOf(
                                hasProperty("id", is(2)),
                                hasProperty("name", is("output-profile-2")),
                                hasProperty("description", is("desc-2"))
                        ),
                        allOf(
                                hasProperty("id", is(3)),
                                hasProperty("name", is("output-profile-3")),
                                hasProperty("description", is("desc-3"))
                        )
                ))));
    }


    @Test
    public void testBFindAll() throws Exception {
        mockMvc.perform(get("/profile/output/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(3)));

    }

    @Test
    public void testBGetOutputProfile() throws Exception {
        OutputProfileDto outputProfileDto = outputProfileService.find(1);
        String json = JsonMapper.getMapper().writeValueAsString(outputProfileDto);
        mockMvc.perform(get("/profile/output/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(json));
    }


    @Test
    public void testCSaveOutputProfile() throws Exception {
        OutputProfileDto outputProfileDto = outputProfileService.find(1);
        outputProfileDto.setId(null);
        outputProfileDto.setName("profile-4");
        outputProfileDto.setDescription("profile-4");
        String json = JsonMapper.getMapper().writeValueAsString(outputProfileDto);
        mockMvc.perform(post("/profile/output")
                        .contentType("application/json;charset=UTF-8")
                        .content(json)
        )
                .andExpect(status().isOk());

        OutputProfileDto newTemplate = outputProfileService.find(7);
        assertNotNull(newTemplate);
        assertEquals(newTemplate.getName(), outputProfileDto.getName());
    }

    @Test
    public void testDForwardWithOp() throws Exception {
        mockMvc.perform(get("/profile/output/edit/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("op", is("edit")))
                .andExpect(model().attribute("profile", notNullValue()));

        mockMvc.perform(get("/profile/output/new/0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("op", is("new")))
                .andExpect(model().attribute("profile", nullValue()));

        mockMvc.perform(get("/profile/output/copy/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("op", is("copy")))
                .andExpect(model().attribute("profile", notNullValue()));
    }

    @Test
    public void testEUpdateOutputProfile() throws Exception {
        OutputProfileDto outputProfileDto = outputProfileService.find(1);
        outputProfileDto.setName("profile-updated");
        String json = JsonMapper.getMapper().writeValueAsString(outputProfileDto);
        mockMvc.perform(put("/profile/output/{id}", outputProfileDto.getId())
                        .contentType("application/json;charset=UTF-8")
                        .content(json)
        )
                .andExpect(status().isOk());

        OutputProfileDto updatedOutputProfileDto = outputProfileService.find(outputProfileDto.getId());
        assertEquals(updatedOutputProfileDto.getName(), outputProfileDto.getName());
    }


    @Test
    public void testFDeleteOutputProfile() throws Exception {
        String[] profileIds = new String[]{"1", "2", "3"};
        mockMvc.perform(delete("/profile/output/{ids}", Joiner.on(",").join(profileIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.code", is(0)));
        int amount = 0;
        for (String id : profileIds) {
            OutputProfileDto outputProfileDto = outputProfileService.find(Integer.valueOf(id));
            if (outputProfileDto == null) {
                amount++;
            }

        }

        assertTrue("Failed to do delete output profile, need delete " + profileIds.length + " items but actual delete "
                + amount, amount == profileIds.length);
    }


}
