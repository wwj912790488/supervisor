package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.model.domain.server.AbstractOpsServer;
import com.arcsoft.supervisor.model.dto.rest.server.OpsServerChannel;
import com.arcsoft.supervisor.service.server.OpsServerService;
import com.arcsoft.supervisor.service.task.TaskService;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.*;

/**
 * @author zw.
 */
public abstract class AbstractOpsApiSupport<T extends AbstractOpsServer> extends RestApiControllerSupport{

    private final OpsServerService<T> opsServerService;
    private final TaskService taskService;

    protected AbstractOpsApiSupport(
            OpsServerService<T> opsServerService,
            TaskService taskService) {
        this.opsServerService = opsServerService;
        this.taskService = taskService;
    }

    public OpsServerService<T> getOpsServerService() {
        return opsServerService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    @RequestMapping(value = "/addops_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String add(@RequestBody String body) {
        saveOrUpdateOpsServer(body);
        return renderSuccessResponse();
    }

    @RequestMapping(value = "/updateops_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String update(@RequestBody String body) {
        saveOrUpdateOpsServer(body);
        return renderSuccessResponse();
    }

    private T convert(String body) {
        T opsServer;
        try {
            opsServer = deserialize(body);
        } catch (Exception e) {
            throw CONVERT_INPUT_ARGUMENTS_FAILED.withException(e);
        }
        if (opsServer == null) {
            throw OPS_INPUT_INCORRECT.exception();
        }
        return opsServer;
    }

    /**
     * Deserialize ops server from json.
     *
     * @return the ops server
     */
    protected abstract T deserialize(String body) throws IOException;

    private void saveOrUpdateOpsServer(String body) {
        T opsServer = convert(body);
        opsServer.setSupportResolutions(StringUtils.join(opsServer.getReslist(), ","));
        opsServerService.save(opsServer);
    }

    @RequestMapping(value = "/getopsinfo_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getOpsChannelInfo(String id) throws IOException, TemplateException {
        if (StringUtils.isBlank(id)) {
            return renderResponseCodeJson(OPS_INPUT_INCORRECT);
        }
        T server = opsServerService.getById(id);
        if (server == null) {
            return renderResponseCodeJson(OPS_UNREGISTERED);
        }
        String validateResult = validateBeforeGetOpsServerChannel(server);
        if (StringUtils.isNotBlank(validateResult)) {
            return validateResult;
        }
        OpsServerChannel opsServerChannel = getOpsServerChannel(server);
        Map<String, Object> model = new HashMap<>();
        model.put("ops", opsServerChannel);
        model.put("statusCode", OK.getCode());
        return freemarkerService.renderFromTemplateFile("ops_channel.ftl", model);
    }

    /**
     * Subclasses can override this method to do some validates before execute
     * {@link #getOpsServerChannel(T)}
     *
     * @param server the ops server
     * @return error message as response or {@code null} if validate is successful
     */
    protected String validateBeforeGetOpsServerChannel(T server) {
        return null;
    }

    /**
     * Subclasses must implement this method to returns {@link OpsServerChannel}
     * with given {@code opsServer}.
     *
     * @param opsServer currently ops server
     * @return the {@link OpsServerChannel} with given {@code opsServer}.
     */
    protected abstract OpsServerChannel getOpsServerChannel(T opsServer);



    @RequestMapping(value = "/removeops_app/{id}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String removeOps(@PathVariable(value = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return renderResponseCodeJson(OPS_INPUT_INCORRECT);
        }
        opsServerService.delete(id);
        return renderSuccessResponse();
    }

}
