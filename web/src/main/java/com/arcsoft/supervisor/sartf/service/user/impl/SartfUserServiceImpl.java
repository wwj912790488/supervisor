package com.arcsoft.supervisor.sartf.service.user.impl;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.user.TokenExpireException;
import com.arcsoft.supervisor.exception.user.TokenNotExistException;
import com.arcsoft.supervisor.exception.user.UserExistsException;
import com.arcsoft.supervisor.exception.user.UserPasswordNotMatchException;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.user.SartfToken;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.sartf.repository.user.SartfUserRepository;
import com.arcsoft.supervisor.sartf.repository.user.SartfTokenRepository;
import com.arcsoft.supervisor.sartf.repository.user.UserConfigRepository;
import com.arcsoft.supervisor.sartf.service.user.SartfUserService;
import com.arcsoft.supervisor.service.user.impl.AbstractUserServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
@Sartf
public class SartfUserServiceImpl extends AbstractUserServiceSupport<SartfUser> implements SartfUserService{

    private final SartfTokenRepository sartfTokenRepository;

    private final UserConfigRepository userconfigRepository;

    private final TaskRepository taskRepository;


    @Autowired
    protected SartfUserServiceImpl(
            SartfUserRepository userRepository,
            SartfTokenRepository sartfTokenRepository,
            UserConfigRepository userconfigRepository,
            TaskRepository taskRepository) {
        super(userRepository);
        this.sartfTokenRepository = sartfTokenRepository;
        this.userconfigRepository = userconfigRepository;
        this.taskRepository = taskRepository;
    }

    public SartfTokenRepository getSartfTokenRepository() {
        return sartfTokenRepository;
    }

    public UserConfigRepository getUserconfigRepository() {
        return userconfigRepository;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    @Override
    public SartfUser newInstance(String userName, String password, Integer role) {
        return new SartfUser(userName, password, role);
    }

    @Override
    public SartfUser register(String userName, String password, Integer role) {
        if (isUserNameExists(userName)){
            throw new UserExistsException(userName);
        }
        SartfUser user = newInstance(userName, password, role);
        ArrayList<UserConfig> configs = new ArrayList<UserConfig>();
        for(int i = 0 ; i < 3 ; i++) {
            UserConfig userconfig = new UserConfig();
            userconfig.setUser(user);
            userconfig.setLastUpdate(new Date());
            configs.add(userconfig);
        }
        user.setConfigs(configs);
        getUserRepository().save(user);
        return user;
    }

    @Override
    public SartfUser login(String userName, String md5Password, boolean generateToken) {
        SartfUser user = getUserRepository().findByUserNameAndPassword(userName, md5Password);

        if (generateToken && user != null) {
            // Generate Token
            SartfToken token;

            token = sartfTokenRepository.findByUserId(user.getId());
            if (token != null) {
                Integer login_count = token.getCount();
                if(login_count == null || login_count == 0) {
                    String tokenStr = TokenKit.getInstance().generateTokenString(
                            user.getId());
                    token.setName(tokenStr);
                }
                token.setCount(login_count == null ? 1 : login_count + 1);
                user.setToken(token.getName());
            } else {
                String tokenStr = TokenKit.getInstance().generateTokenString(
                        user.getId());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date;
                try {
                    date = df.parse(df.format(new Date()));
                } catch (Exception e) {
                    date = new Date();
                }

                token = new SartfToken(tokenStr, user, date.getTime());
                sartfTokenRepository.save(token);

                user.setToken(token.getName());
            }
            String saveToken = user.getToken();
            UserConfig userconfig = user.getCurrent();
        }

        return user;
    }

    @Override
    public void logout(Integer userId) {
        SartfToken token = sartfTokenRepository.findByUserId(userId);
        Integer login_count = token.getCount();
        if(login_count == null || login_count == 0) {
            token.setCount(0);
        } else {
            token.setCount(login_count - 1);
        }
    }

    @Override
    public SartfUser updatepwd(String token, String oldpwd, String newpwd) {
        SartfUser oldUser = getUserRepository().findOne(getIdByToken(token));
        if(oldUser != null && oldUser.getPassword().equals(oldpwd))
        {
            oldUser.setPassword(newpwd);
            getUserRepository().save(oldUser);
            return oldUser;
        }
        throw new UserPasswordNotMatchException();
    }

    @Override
    public void updateBindOps(Integer userId, String opsId) {

    }

    @Override
    public void updateUnbindOps(Integer userId, String opsId) {

    }

    @Override
    public void updateOps(Integer userId, SartfOpsServer ops) {
        SartfUser user = getUserRepository().findOne(userId);
        user.setOps(ops);
    }

    @Override
    public SartfUser getUserByToken(String tokenstr) {
        SartfToken token = sartfTokenRepository.findByName(tokenstr);
        if(token == null)
            throw new TokenNotExistException();

        Date date = new Date();
        if((date.getTime() - token.getCreate_time()) > EXPIRE_TIME)
        {
            sartfTokenRepository.delete(token.getId());
            throw new TokenExpireException();
        }

        return (SartfUser) token.getUser();
    }

    @Override
    public Integer getIdByToken(String tokenstr) {
        //check the token DB
        SartfToken token = sartfTokenRepository.findByName(tokenstr);
        if(token == null)
            throw new TokenNotExistException();

        Date date = new Date();
        if((date.getTime() - token.getCreate_time()) > EXPIRE_TIME)
        {
            sartfTokenRepository.delete(token.getId());
            throw new TokenExpireException();
        }

        return token.getUser().getId();
    }

    @Override
    public void updateUserCurrentConfig(Integer userId, Integer config_id) {
        SartfUser oldUser = getUserRepository().findOne(userId);
        if(oldUser != null) {
            oldUser.setCurrent(userconfigRepository.findOne(config_id));
        }
    }

    @Override
    public String getTokenById(Integer userId) {
        SartfToken token = sartfTokenRepository.findByUserId(userId);
        return token.getName();
    }

    @Override
    public void deleteTaskById(Integer userId) {
        taskRepository.deleteByTypeAndReferenceId(TaskType.USER_RELATED_COMPOSE.getType(), userId);
    }
}
