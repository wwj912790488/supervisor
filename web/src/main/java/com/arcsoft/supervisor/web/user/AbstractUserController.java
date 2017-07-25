package com.arcsoft.supervisor.web.user;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.exception.user.UserExistsException;
import com.arcsoft.supervisor.exception.user.UserPasswordNotMatchException;
import com.arcsoft.supervisor.model.domain.user.AbstractUser;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.user.UserService;
import com.arcsoft.supervisor.web.ControllerSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Base controller class for <tt>User</tt> module.
 *
 * @author zw.
 */
@RequestMapping("/user")
public abstract class AbstractUserController<T extends AbstractUser> extends ControllerSupport{

    public static final String VIEW_INDEX = "/user/index";
    public static final String VIEW_CHANGE_PASSWORD = "/user/chgPswd";

    protected final UserService<T> userService;

    protected AbstractUserController(UserService<T> userService) {
        this.userService = userService;
    }

    public UserService<T> getUserService() {
        return userService;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(){
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/chgPswd", method = RequestMethod.GET)
    public String changepswd(Model model, HttpServletRequest request){
        return VIEW_CHANGE_PASSWORD;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<T> getUsers() {
        return userService.listAll();
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ResponseBody
    public String addUser(User user) {
        try {
            T added = userService.register(user.getUserName(), user.getPassword(), user.getRole());
            return added.getId().toString();
        }
        catch(UserExistsException e) {
            return "";
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public void deleteUsers(String usersStr) {
        try {
            List<T> users = JsonMapper.getMapper().readValue(
                    usersStr,
                    JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, getClassOfBondedType())
            );
            if (users != null) {
                doDeleteUsers(users);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns class of currently bonded generic type
     *
     * @return the class of currently bonded generic type
     */
    protected abstract Class<?> getClassOfBondedType();


    /**
     * Subclasses can overridden this method to perform delete users.
     *
     * @param users the users to be deleted
     */
    protected abstract void doDeleteUsers(List<T> users);

    @RequestMapping(value = "/editUserRole", method = RequestMethod.POST)
    @ResponseBody
    public String editUserRole(T user) {
        try {
            T edited = userService.updateUserRole(user);
            return edited.getId().toString();
        } catch (Exception e) {
            return "";
        }
    }

    @RequestMapping(value = "/editUserPswd", method = RequestMethod.POST)
    @ResponseBody
    public String editUserPswd(T user) {
        try {
            T edited = userService.updateUserPswd(user);
            return edited.getId().toString();
        } catch (UserPasswordNotMatchException e) {
            return "";
        }
    }

    @RequestMapping(value = "/resetUserPswd", method = RequestMethod.POST)
    @ResponseBody
    public String resetUserPswd(T user) {
        try {
            T edited = userService.updateUserNewPswd(user);
            return edited.getId().toString();
        } catch (Exception e) {
            return "";
        }
    }

}
