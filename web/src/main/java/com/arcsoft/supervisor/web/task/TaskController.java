package com.arcsoft.supervisor.web.task;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.task.TaskDispatcherFacade;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller class for task.
 *
 * @author zw.
 */
@Controller
@Production
public class TaskController extends AbstractTaskController {

    private final TaskExecutor taskExecutor;

    @Autowired
    protected TaskController(
            TaskDispatcherFacade taskDispatcherFacade,
            ScreenService screenService,
            TaskService taskService,
            TaskExecutor taskExecutor) {
        super(taskDispatcherFacade, screenService, taskService);
        this.taskExecutor = taskExecutor;
    }

    @RequestMapping(value = "/screen/usedTaskProfile/{screenId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getUsedTaskProfileId(@PathVariable Integer screenId) {
        return JsonResult.fromSuccess()
                .put(KEY_OF_RESULT, getTaskService().getUsedTaskProfileIdByScreenId(screenId));
    }

    @RequestMapping(value = "/reload/{screenId}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult reloadTask(@PathVariable Integer screenId) {
        taskExecutor.reload(getTaskService().getById(screenId).getReferenceId());
        return JsonResult.fromSuccess();
    }

}
