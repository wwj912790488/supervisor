package com.arcsoft.supervisor.web.user;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * @author zw.
 */
@Controller
@Production
public class UserController extends AbstractUserController<User> {

    @Autowired
    protected UserController(UserService<User> userService) {
        super(userService);
    }

    @Override
    protected Class<?> getClassOfBondedType() {
        return User.class;
    }

    @Override
    protected void doDeleteUsers(List<User> users) {
        getUserService().deleteByIds(users);
    }


}
