package com.arcsoft.supervisor.service.user.impl;

import com.arcsoft.supervisor.exception.user.UserExistsException;
import com.arcsoft.supervisor.exception.user.UserPasswordNotMatchException;
import com.arcsoft.supervisor.model.domain.user.AbstractUser;
import com.arcsoft.supervisor.repository.user.BaseUserRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.user.UserService;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

/**
 * @author zw.
 */
public abstract class AbstractUserServiceSupport<T extends AbstractUser> extends ServiceSupport
        implements UserService<T>, TransactionSupport {

    //	24*60*60*1000
    protected static final long SINGLE_DATETIME = 86400000;
    protected static final long EXPIRE_TIME = SINGLE_DATETIME*30;

    private final BaseUserRepository<T> userRepository;

    public AbstractUserServiceSupport(BaseUserRepository<T> userRepository) {
        this.userRepository = userRepository;
    }

    public BaseUserRepository<T> getUserRepository() {
        return userRepository;
    }

    @Override
    public T register(String userName, String password) {
        return register(userName, password, 0);
    }

    @Override
    public T register(String userName, String password, Integer role) {
        if (isUserNameExists(userName)) {
            throw new UserExistsException(userName);
        }
        T user = newInstance(userName, password, role);
        userRepository.save(user);
        return user;
    }

    @Override
    public T login(String userName, String md5Password) {
        return userRepository.findByUserNameAndPassword(userName, md5Password);
    }

    @Override
    public void delete(T user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteById(Integer id) {
        try {
            userRepository.delete(id);
        } catch (EmptyResultDataAccessException e) {
            //Ignore this exception if the entity not exists.
        }

    }

    @Override
    public void deleteByIds(List<T> users) {
        userRepository.deleteInBatch(users);
    }

    @Override
    public boolean isUserNameExists(String userName) {
        Long c = userRepository.countByUserName(userName);
        return c != null && c > 0;
    }

    @Override
    public List<T> listAll() {
        return userRepository.findAll();
    }

    @Override
    public T updateUserPswd(T user) {
        T oldUser = userRepository.findOne(user.getId());
        if (oldUser != null && oldUser.getPassword().equals(user.getPassword())) {
            oldUser.setPassword(user.getNewPassword());
            return oldUser;
        }
        throw new UserPasswordNotMatchException();
    }

    @Override
    public T updateUserRole(T user) {
        T oldUser = userRepository.findOne(user.getId());
        if (oldUser != null) {
            oldUser.setRole(user.getRole());
        }
        return oldUser;
    }

    @Override
    public T updateUserNewPswd(T user) {
        T oldUser = userRepository.findOne(user.getId());
        if (oldUser != null) {
            oldUser.setPassword(user.getNewPassword());
        }
        return oldUser;
    }
}
