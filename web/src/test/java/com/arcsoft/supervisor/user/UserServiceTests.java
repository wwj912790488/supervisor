package com.arcsoft.supervisor.user;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.user.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * A tests for {@link com.arcsoft.supervisor.service.user.UserService}.
 *
 * @author zw.
 */
public class UserServiceTests extends ProductionTestSupport {

    @Autowired
    private UserService<User> userService;

    @Test
    @Transactional
    public void test() {
        userService.deleteById(1);
        User user = userService.register("admin", DigestUtils.md5Hex("123456"));

        assertNotNull(user);
        User loginUser = userService.login("admin", DigestUtils.md5Hex("123456"));
        assertEquals(user.getId(), loginUser.getId());
        assertTrue(userService.isUserNameExists("admin"));
        userService.delete(loginUser);

    }
}
