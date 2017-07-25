package com.arcsoft.supervisor.service.user;

import com.arcsoft.supervisor.model.domain.user.User;

import java.util.List;

/**
 * Extended UserService used in <tt>Production</tt> profile.
 *
 * @author zw.
 */
public interface ProductionUserService {

    /**
     * Update user's <tt>phoneNumber</tt> with given id.
     *
     * @param id          the identifier of user
     * @param phoneNumber the phone number
     */
    void updateUserPhoneNumber(int id, String phoneNumber);

    /**
     * Retrieves all of phone number of users.
     *
     * @return all of phone number of users
     */
    List<String> getAllPhoneNumbers();

    /**
     * Returns the user with given id.
     *
     * @param id the identifier
     * @return the user with given id
     */
    User findUser(int id);
}
