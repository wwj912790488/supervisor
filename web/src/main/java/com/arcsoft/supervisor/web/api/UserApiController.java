package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
/**
 * Controller class for rest api of {@code user}.
 *
 * @author zw.
 */
@Controller
@Production
public class UserApiController extends AbstractUserApiSupport<User> {


    @Autowired
    protected UserApiController(UserService<User> userService) {
        super(userService);
    }

    @Override
    protected User doLogin(String userName, String password,boolean generateToken) {
        return getUserService().login(userName, password,generateToken);
    }
}
