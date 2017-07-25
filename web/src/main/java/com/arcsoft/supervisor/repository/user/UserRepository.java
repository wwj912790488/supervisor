package com.arcsoft.supervisor.repository.user;

import com.arcsoft.supervisor.model.domain.user.User;

import java.util.List;

/**
 * Repository interface for <tt>User</tt>
 *
 * @author zw.
 */
public interface UserRepository extends BaseUserRepository<User> {

    List<User> findByPhoneNumberNotNull();

}
