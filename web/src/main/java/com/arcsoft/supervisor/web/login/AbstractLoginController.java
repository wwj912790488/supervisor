package com.arcsoft.supervisor.web.login;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.exception.user.UserExistsException;
import com.arcsoft.supervisor.model.domain.user.AbstractUser;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.log.SystemLogService;
import com.arcsoft.supervisor.service.user.UserService;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zw.
 */
@RequestMapping("/login")
public abstract class AbstractLoginController<T extends AbstractUser> extends ControllerSupport {

    private static final String VIEW_INDEX = "/login/index";

    private final UserService<T> userService;

    private final SystemLogService systemLogService;

    public AbstractLoginController(UserService<T> userService, SystemLogService systemLogService) {
        this.userService = userService;
        this.systemLogService = systemLogService;
    }

    public UserService<T> getUserService() {
        return userService;
    }

    public SystemLogService getSystemLogService() {
        return systemLogService;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(HttpServletResponse response) {
        response.addHeader("Redirect_Location", VIEW_INDEX);
        return VIEW_INDEX;
    }

    @RequestMapping(value = "out")
    public String userLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO);
        }
        return VIEW_INDEX;
    }


    @RequestMapping(value = "/sign_in")
    @ResponseBody
    public JsonResult userSignin(HttpServletRequest request, String userName, String password) throws IOException {

        if (userName != null) {
            userName = new String(userName.getBytes("iso-8859-1"), "UTF-8");
            T user = userService.login(userName, password);
            if (user != null) {
                request.getSession().setAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO, user);
                Date datetime = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strdatetime = sdf.format(datetime);
                systemLogService.add(strdatetime, userName, 4, "用户登录", "成功");
                return JsonResult.fromSuccess();
            }
        }

        return JsonResult.fromError();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult AddUser(User user, HttpServletRequest request) throws IOException {
        try {
            T added = userService.register(user.getUserName(), user.getPassword());
            request.getSession().setAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO, added);
            return JsonResult.fromSuccess();
        } catch (UserExistsException e) {
            return JsonResult.fromError();
        }
    }

}
