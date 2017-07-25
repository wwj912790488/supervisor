package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.exception.user.UserExistsException;
import com.arcsoft.supervisor.model.domain.user.AbstractUser;
import com.arcsoft.supervisor.service.user.UserService;
import com.arcsoft.supervisor.utils.app.Environment;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.*;

/**
 * @author zw.
 */
public abstract class AbstractUserApiSupport<T extends AbstractUser> extends RestApiControllerSupport{

    private final UserService<T> userService;

    protected AbstractUserApiSupport(UserService<T> userService) {
        this.userService = userService;
    }

    public UserService<T> getUserService() {
        return userService;
    }

    @RequestMapping(value = "/register_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String register(@RequestParam(value = "username", required = false) String userName, String password) {
        // TODO: Improve it
        // Just return error because we don't need this functionality
        throw ERROR.exception();
//        check(userName, password);
//        try {
//            userService.register(userName, password);
//        } catch (UserExistsException e) {
//            throw USER_REGISTER_EXISTS.withException(e);
//        }
//        return renderSuccessResponse();
    }

    @RequestMapping(value = "/login_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String login(@RequestParam(value = "username", required = false) String userName, String password) throws
            IOException, TemplateException {
        check(userName, password);
        T user = doLogin(userName, password,false);
        if (user == null) {
            return renderResponseCodeJson(USER_LOGIN_NAME_OR_PASSWORD_INCORRECT);
        }
        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", OK.getCode());
        model.put("user", user);

        model.put("schema",Integer.valueOf(Environment.getProperty("mobile.schema","0"))!=0?"true":"false");
        model.put("startstop",Integer.valueOf(Environment.getProperty("mobile.startstop","0"))!=0?"true":"false");

        return freemarkerService.renderFromTemplateFile("user.ftl", model);
    }

    /**
     * Template method to do login and return matched user.
     *
     * @return {@code User} or {@code null} if not match
     */
    protected abstract T doLogin(String userName, String password, boolean generateToken);

    private void check(String userName, String password) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            throw INVALID_ARGUMENTS.exception();
        }
    }


    @RequestMapping(value = "/logout_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String loginOut(String token) {
        return renderSuccessResponse();
    }
}
