package com.arcsoft.supervisor.sartf.web.api;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.sartf.service.server.SartfOpsServerOperator;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskDispatcherFacade;
import com.arcsoft.supervisor.sartf.service.user.SartfUserService;
import com.arcsoft.supervisor.web.api.AbstractUserApiSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.ERROR;
import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.INVALID_ARGUMENTS;

@Controller
@Sartf
public class UserApiController extends AbstractUserApiSupport<SartfUser> {

    private final SartfUserService userService;
    private final SartfOpsServerOperator opsServerOperator;
    private final TransactionTemplate transactionTemplate;
    private final SartfTaskDispatcherFacade taskDispatcherFacade;

    @Autowired
    protected UserApiController(
            SartfUserService userService,
            SartfOpsServerOperator opsServerOperator,
            TransactionTemplate transactionTemplate,
            SartfTaskDispatcherFacade taskDispatcherFacade) {
        super(userService);
        this.userService = userService;
        this.opsServerOperator = opsServerOperator;
        this.transactionTemplate = transactionTemplate;
        this.taskDispatcherFacade = taskDispatcherFacade;
    }

    @Override
    protected SartfUser doLogin(String userName, String password,boolean generateToken) {
        SartfUser user = userService.login(userName, password, true);
        if (user != null && user.getCurrent() == null) {
            try {
                taskDispatcherFacade.startUserTask(user.getId());
            } catch(BusinessException e) {
                userService.logout(user.getId());
                throw e;
            }
        }
        return user;
    }

    @RequestMapping(value = "/changepwd_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String changepwd(@RequestParam(value = "token", required = true) String token, String oldpwd, String newpwd) {
        check(token, oldpwd, newpwd);

        SartfUser user = userService.updatepwd(token, oldpwd, newpwd);
        if (user == null) {
            return renderResponseCodeJson(ERROR);
        }

        return renderSuccessResponse();
    }

    private void check(String token, String oldpwd, String newpwd) {
        if (StringUtils.isBlank(token) || StringUtils.isBlank(oldpwd) || StringUtils.isBlank(newpwd)) {
            throw INVALID_ARGUMENTS.exception();
        }
    }

    @Override
    public String loginOut(String token) {
        Integer userId = userService.getIdByToken(token);
        try {
            taskDispatcherFacade.stopUserTask(userId);
        } finally {
            informOpsInTransaction(token);

            userService.deleteTaskById(userId);
            userService.logout(userId);
        }
        return renderSuccessResponse();
    }

    private void informOpsInTransaction(final String token) {
        transactionTemplate.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                SartfUser user = userService.getUserByToken(token);
                SartfOpsServer server = user.getOps();
                opsServerOperator.stop(server.getIp(), server.getPort());
                return null;
            }
        });
    }
}
