package com.arcsoft.supervisor.web.login;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.log.SystemLogService;
import com.arcsoft.supervisor.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Production
public class LoginController extends AbstractLoginController<User> {

	@Autowired
	public LoginController(
			UserService<User> userService,
			SystemLogService systemLogService) {
		super(userService, systemLogService);
	}

}
