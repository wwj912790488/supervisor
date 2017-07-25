package com.arcsoft.supervisor.sartf.web.user;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.sartf.service.server.SartfOpsServerOperator;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskDispatcherFacade;
import com.arcsoft.supervisor.sartf.service.user.SartfUserService;
import com.arcsoft.supervisor.service.server.OpsServerService;
import com.arcsoft.supervisor.web.user.AbstractUserController;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@Sartf
public class UserController extends AbstractUserController<SartfUser> {

    private final OpsServerService<SartfOpsServer> opsServerService;

    private final SartfOpsServerOperator opsServerOperator;

    private final TransactionTemplate transactionTemplate;

    private final SartfTaskDispatcherFacade taskDispatcherFacade;

    private final SartfUserService sartfUserService;

    @Autowired
    protected UserController(
            SartfUserService userService,
            OpsServerService<SartfOpsServer> opsServerService,
            SartfOpsServerOperator opsServerOperator,
            TransactionTemplate transactionTemplate,
            SartfTaskDispatcherFacade taskDispatcherFacade) {
        super(userService);
        this.sartfUserService = userService;
        this.opsServerService = opsServerService;
        this.opsServerOperator = opsServerOperator;
        this.transactionTemplate = transactionTemplate;
        this.taskDispatcherFacade = taskDispatcherFacade;
    }


    public String index(Model model, HttpServletRequest request) throws JsonProcessingException {
        List<SartfOpsServer> opsList = opsServerService.findAll();
        model.addAttribute("opsList", JsonMapper.getMapper().writeValueAsString(opsList));
        model.addAttribute("login_info", request.getSession().getAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO));
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/chgPswd", method = RequestMethod.GET)
    public String changepswd(Model model, HttpServletRequest request){
        model.addAttribute("login_info", request.getSession().getAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO));
        return VIEW_CHANGE_PASSWORD;
    }

    @Override
    protected Class<?> getClassOfBondedType() {
        return SartfUser.class;
    }

    @Override
    protected void doDeleteUsers(List<SartfUser> users) {
        for(SartfUser user : users) {
            try {
                taskDispatcherFacade.stopUserTask(user.getId());
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            sartfUserService.deleteTaskById(user.getId());
        }
    }

    @RequestMapping(value = "/bindOps", method = RequestMethod.POST)
    @ResponseBody
    public void bindOps(Integer userId, String opsId) {
        sartfUserService.updateOps(userId, opsServerService.getById(opsId));
    }

    @RequestMapping(value = "/unbindOps", method = RequestMethod.POST)
    @ResponseBody
    public void unbindOps(Integer userId) {
        String token = sartfUserService.getTokenById(userId);
        informOpsInTransaction(token);
        sartfUserService.updateOps(userId, null);
        try {
            taskDispatcherFacade.stopUserTask(userId);
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        sartfUserService.deleteTaskById(userId);
    }

    private void informOpsInTransaction(final String token) {
        transactionTemplate.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                SartfUser user = sartfUserService.getUserByToken(token);
                SartfOpsServer server = user.getOps();
                opsServerOperator.stop(server.getIp(), server.getPort());
                return null;
            }
        });
    }

    @RequestMapping(value = "ops", method = RequestMethod.GET)
    @ResponseBody
    public List<SartfOpsServer> getOpsList() {
        return opsServerService.findAll();
    }


}
