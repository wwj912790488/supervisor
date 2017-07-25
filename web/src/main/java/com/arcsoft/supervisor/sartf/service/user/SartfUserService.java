package com.arcsoft.supervisor.sartf.service.user;

import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.service.user.UserService;


public interface SartfUserService extends UserService<SartfUser> {

    /**
     * Login a user with <tt>userName</tt> and <tt>password</tt>.
     *
     * @param userName    the username
     * @param md5Password the md5 password string
     * @return the exists user or {@code null} if there is not exists a user with
     * <tt>userName</tt> and <tt>md5Password</tt>
     */
    SartfUser login(String userName, String md5Password, boolean generateToken);

    void logout(Integer userId);

    SartfUser updatepwd(String token, String oldpwd, String newpwd);

    void updateBindOps(Integer userId, String opsId);

    void updateUnbindOps(Integer userId, String opsId);

    void updateOps(Integer userId, SartfOpsServer ops);

    void updateUserCurrentConfig(Integer userId, Integer config_id);

    String getTokenById(Integer userId);

    void deleteTaskById(Integer userId);
}
