package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.WallPosition;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.dto.rest.server.OpsServerChannel;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.service.graphic.WallService;
import com.arcsoft.supervisor.service.server.OpsServerService;
import com.arcsoft.supervisor.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.OPS_SCREEN_DELETED;


/**
 * @author zw.
 */
@Controller
@Production
public class OpsApiController extends AbstractOpsApiSupport<OpsServer> {

    private final WallService wallService;

    @Autowired
    protected OpsApiController(
            OpsServerService<OpsServer> opsServerService,
            TaskService taskService,
            WallService wallService) {
        super(opsServerService, taskService);
        this.wallService = wallService;
    }

    private Screen getScreen(String opsServerId) {
        WallPosition position = wallService.getWallPositionWithOpsServerId(opsServerId);
        return position != null ? position.getScreen() : null;
    }

    @Override
    protected String validateBeforeGetOpsServerChannel(OpsServer server) {
        Screen screen = getScreen(server.getId());
        if (screen == null) {
            return renderResponseCodeJson(OPS_SCREEN_DELETED);
        }
        return null;
    }

    @Override
    protected OpsServerChannel getOpsServerChannel(OpsServer opsServer) {
        Screen screen = getScreen(opsServer.getId());
        return createOpsServerChannel(
                opsServer,
                screen,
                getTaskService().getByTypeAndReferenceId(
                        screen.getId(),
                        TaskType.IP_STREAM_COMPOSE
                )
        );
    }

    private OpsServerChannel createOpsServerChannel(OpsServer server, Screen screen, Task task) {
        OpsServerChannel opsServerChannel = new OpsServerChannel();
        opsServerChannel.setId(server.getId());
        opsServerChannel.setChannelId("");
        opsServerChannel.setChannelName("");
        String pushUrl = screen.getPushUrl();
        opsServerChannel.setUrl(task != null && task.isStatusEqual(TaskStatus.RUNNING) ? ((pushUrl!=null && !pushUrl.isEmpty()) ?pushUrl:screen.getAddress()) : "");
        opsServerChannel.setRect("0,0,1,1");
        return opsServerChannel;
    }

    @Override
    protected OpsServer deserialize(String body) throws IOException {
        return JsonMapper.getMapper().readValue(body, OpsServer.class);
    }

}
