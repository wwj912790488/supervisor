package com.arcsoft.supervisor.sartf.web.login;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.service.log.SystemLogService;
import com.arcsoft.supervisor.service.user.UserService;
import com.arcsoft.supervisor.web.login.AbstractLoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author zw.
 */
@Controller
@Sartf
public class LoginController extends AbstractLoginController<SartfUser> {

    @Autowired
    public LoginController(
            UserService<SartfUser> userService,
            SystemLogService systemLogService) {
        super(userService, systemLogService);
    }

}
