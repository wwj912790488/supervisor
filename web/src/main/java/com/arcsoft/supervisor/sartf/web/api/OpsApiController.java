package com.arcsoft.supervisor.sartf.web.api;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.dto.rest.server.OpsServerChannel;
import com.arcsoft.supervisor.sartf.repository.user.SartfUserRepository;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskService;
import com.arcsoft.supervisor.service.server.OpsServerService;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.web.api.AbstractOpsApiSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Sartf
public class OpsApiController extends AbstractOpsApiSupport<SartfOpsServer> {

    private final SartfUserRepository userRepository;
    private final RtspConfigurationService rtspConfigurationService;

    @Autowired
    protected OpsApiController(
            OpsServerService<SartfOpsServer> opsServerService,
            SartfTaskService taskService,
            SartfUserRepository userRepository,
            RtspConfigurationService rtspConfigurationService) {
        super(opsServerService, taskService);
        this.userRepository = userRepository;
        this.rtspConfigurationService = rtspConfigurationService;
    }

    @Override
    protected OpsServerChannel getOpsServerChannel(SartfOpsServer opsServer) {
        SartfUser user = userRepository.findByOps(opsServer);
        return createOpsServerChannel(opsServer, user);
    }

    private OpsServerChannel createOpsServerChannel(SartfOpsServer server, SartfUser user) {
        OpsServerChannel opsServerChannel = new OpsServerChannel();
        opsServerChannel.setId(server.getId());
        opsServerChannel.setChannelId("");
        opsServerChannel.setChannelName("");
        opsServerChannel.setUrl(
                (user != null && user.getInfo() != null)
                        ? rtspConfigurationService.composeUrl(user.getInfo().getRtspOpsFileName(), server.getIp())
                        : ""
        );
        opsServerChannel.setRect("0,0,1,1");
        return opsServerChannel;
    }

    @Override
    protected SartfOpsServer deserialize(String body) throws IOException {
        return JsonMapper.getMapper().readValue(body, SartfOpsServer.class);
    }
}
