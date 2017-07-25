package com.arcsoft.supervisor.web.profile;


import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.model.domain.system.GpuConfiguration;
import com.arcsoft.supervisor.model.domain.task.OutputProfile;
import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.model.vo.task.profile.OutputProfileDto;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.service.log.SystemLogService;
import com.arcsoft.supervisor.service.profile.ProfileService;
import com.arcsoft.supervisor.service.profile.TaskProfileService;
import com.arcsoft.supervisor.service.settings.ConfigurationService;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.arcsoft.supervisor.commons.SupervisorDefs.Constants.PAGER;
import static com.arcsoft.supervisor.commons.SupervisorDefs.Constants.QUERY_PARAMS;

@Controller
@RequestMapping("/profile")
public class ProfileController extends ControllerSupport {

    public static final String VIEW_OUTPUT_INDEX = "/profile/outputlist";
    public static final String VIEW_OUTPUT_ITEM = "/profile/output";
    public static final String VIEW_TASK_INDEX = "/profile/tasklist";
    public static final String VIEW_TASK_ITEM = "/profile/task";
    public static final String OP_EDIT = "edit";
    public static final String OP_NEW = "new";
    public static final String OP_COPY = "copy";
    public static final List<String> OPERATORS = ImmutableList.of(OP_COPY, OP_EDIT, OP_NEW).asList();

    private ProfileService<OutputProfile, OutputProfileDto> outputProfileService;

    private TaskProfileService taskProfileService;

    @Autowired
    private SystemLogService systemLogService;

    private ConfigurationService<GpuConfiguration> gpuConfigurationService;

    @Autowired
    public ProfileController(
            @Qualifier("defaultOutputProfileService")
            ProfileService<OutputProfile, OutputProfileDto> outputProfileService,
            @Qualifier("defaultTaskProfileService")
            TaskProfileService taskProfileService,
            @Qualifier("gpuConfigurationService")
            ConfigurationService<GpuConfiguration> gpuConfigurationService) {
        this.outputProfileService = outputProfileService;
        this.taskProfileService = taskProfileService;
        this.gpuConfigurationService = gpuConfigurationService;
    }

    //for output profile

    @RequestMapping(value = "/output", method = RequestMethod.GET)
    public String output(Model model, @PageableDefault(sort = {"id"},size=100,
            direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute(SupervisorDefs.Constants.PAGER, outputProfileService.paginate((PageRequest) pageable));
        return VIEW_OUTPUT_INDEX;
    }

    @RequestMapping(value = "/output/all", method = RequestMethod.GET)
    @ResponseBody
    public List<OutputProfileDto> findAllOutput() {
        return outputProfileService.findAll();
    }

    @RequestMapping(value = "/output/{id}", method = RequestMethod.GET)
    @ResponseBody
    public OutputProfileDto getOutputProfile(@PathVariable("id") Integer id) {
        return outputProfileService.find(id);
    }

    @RequestMapping(value = {"/output", "/output/{id}"}, method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public JsonResult saveOutputProfile(@RequestBody OutputProfileDto outputProfileDto) {
        outputProfileService.save(outputProfileDto);
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/output/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResult deleteOutputProfile(@PathVariable("ids") Integer[] ids) {
        outputProfileService.deleteAll(Arrays.asList(ids));
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/output/{op}/{id}", method = RequestMethod.GET)
    public String editOutputProfile(@PathVariable("op") String op,
                                    @PathVariable(value = "id") Integer id,
                                    Model model) throws JsonProcessingException {
        if (!OPERATORS.contains(op)) {
            //todo: do exception handler
        }
        model.addAttribute("op", op);
        if (id == null || id == 0) { //Indicates is operate of new
            return VIEW_OUTPUT_ITEM;
        }
        OutputProfileDto outputProfileDto = outputProfileService.find(id);
        if (outputProfileDto != null) {
            switch (op) {
                case OP_COPY:
                    outputProfileDto.setId(null);
                    break;
            }
            model.addAttribute("profile", JsonMapper.getMapper().writeValueAsString(outputProfileDto));
        }
        return VIEW_OUTPUT_ITEM;
    }

    //for task profile

    @RequestMapping(value = "/task")
    public String task(Model model, @PageableDefault(sort = {"id"},size=15, direction = Sort.Direction.DESC) Pageable pageable) {

        model.addAttribute(PAGER, taskProfileService.paginate((PageRequest) pageable));
        model.addAttribute(QUERY_PARAMS, null);
        return VIEW_TASK_INDEX;
    }

    @RequestMapping(value = "/task/{id}", method = RequestMethod.GET)
    @ResponseBody
    public TaskProfileDto getTaskProfile(@PathVariable("id") Integer id) {
        return taskProfileService.find(id);
    }

    @RequestMapping(value = {"/task", "/task/{id}"}, method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public JsonResult saveTaskProfile(ServletRequest request,@RequestBody TaskProfileDto taskProfileDto) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Date datetime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strdatetime = sdf.format(datetime);
        User user = (User) httpServletRequest.getSession().getAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO);
        String strUserName = (user == null) ? "Unlogin" : user.getUserName();
        systemLogService.add(strdatetime, strUserName, 7, "模板保存", "成功");

        taskProfileService.save(taskProfileDto);
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/task/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResult deleteTaskProfiles(ServletRequest request, @PathVariable("ids") Integer[] ids) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Date datetime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strdatetime = sdf.format(datetime);
        User user = (User) httpServletRequest.getSession().getAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO);
        String strUserName = (user == null) ? "Unlogin" : user.getUserName();
        systemLogService.add(strdatetime, strUserName, 7, "模板删除", "成功");

        taskProfileService.deleteAll(Arrays.asList(ids));
        outputProfileService.deleteAll(Arrays.asList(ids));
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/task/{op}/{id}", method = RequestMethod.GET)
    public String editTaskProfile(@PathVariable("op") String op,
                                    @PathVariable(value = "id") Integer id,
                                    Model model) throws JsonProcessingException {
        if (!OPERATORS.contains(op)) {
            //todo: do exception handler
        }
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic",mosaic);
        model.addAttribute("op", op);
        if (id == null || id == 0) { //Indicates is operate of new
            return VIEW_TASK_ITEM;
        }
        TaskProfileDto taskProfileDto = taskProfileService.find(id);
        if (taskProfileDto != null) {
            switch (op) {
                case OP_COPY:
                    taskProfileDto.setId(null);
                    break;
            }
            model.addAttribute("profile", JsonMapper.getMapper().writeValueAsString(taskProfileDto));
        }
        return VIEW_TASK_ITEM;
    }

    @RequestMapping(value = "/task/layout/{row}/{column}", method = RequestMethod.GET)
    @ResponseBody
    public List<TaskProfile> queryByScreenLayout(@PathVariable Integer row, @PathVariable Integer column) {
        return taskProfileService.findByLayout(row, column);
    }

    @RequestMapping(value = "/task/list", method = RequestMethod.GET)
    @ResponseBody
    public List<TaskProfile> getTaskProfileList() {
        return taskProfileService.findAllTaskProfile();
    }

}
