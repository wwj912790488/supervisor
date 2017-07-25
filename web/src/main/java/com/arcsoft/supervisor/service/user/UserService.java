package com.arcsoft.supervisor.service.user;

import com.arcsoft.supervisor.exception.user.UserExistsException;
import com.arcsoft.supervisor.model.domain.user.AbstractUser;
import com.arcsoft.supervisor.model.domain.user.User;

import java.util.List;

/**
 * Interface for handling logic of <tt>user</tt>.
 *
 * @author zw.
 */
public interface UserService<T extends AbstractUser> {

    /**
     * Returns a new instance.
     *
     * @param userName the name of user
     * @param password the password of user
     * @param role the role of user
     * @return a newly instance
     */
    T newInstance(String userName, String password, Integer role);

    /**
     * Register a user with <tt>username</tt> and <tt>password</tt>
     *
     * @param userName the username
     * @param password the md5 password string
     * @return the registered user
     * @throws UserExistsException if the {@code userName} is exists
     */
    T register(String userName, String password) throws UserExistsException;

    T register(String userName, String password, Integer role) throws UserExistsException;

    /**
     * Login a user with <tt>userName</tt> and <tt>password</tt>.
     *
     * @param userName    the username
     * @param md5Password the md5 password string
     * @return the exists user or {@code null} if there is not exists a user with
     * <tt>userName</tt> and <tt>md5Password</tt>
     */
    T login(String userName, String md5Password);
    
    T login(String userName, String md5Password, boolean generateToken);

    /**
     * Removes a user.
     *
     * @param user the user instance will be remove
     */
    void delete(T user);

    /**
     * Removes a user with the specify <tt>id</tt> identify.
     *
     * @param id
     */
    void deleteById(Integer id);

    void deleteByIds(List<T> users);

    /**
     * Retrieves the user with the specify <tt>userName</tt>
     *
     * @param userName the username of user
     * @return {@code true} or {@code false} if the userName is not existed
     */
    boolean isUserNameExists(String userName);

    T updateUserRole(T user);

    T updateUserPswd(T user);

    T updateUserNewPswd(T user);

    List<T> listAll();
    
    Integer getIdByToken(String token);

    T getUserByToken(String tokenstr);
    
    void logout(Integer userId);
    
}
