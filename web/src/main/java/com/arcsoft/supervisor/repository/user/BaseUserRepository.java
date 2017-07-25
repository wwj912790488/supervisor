package com.arcsoft.supervisor.repository.user;

import com.arcsoft.supervisor.model.domain.user.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author zw.
 */
@NoRepositoryBean
public interface BaseUserRepository<T extends AbstractUser> extends JpaRepository<T, Integer> {

    T findByUserNameAndPassword(String userName, String password);

    Long countByUserName(String userName);
}
