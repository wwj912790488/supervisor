package com.arcsoft.supervisor.exception.user;

import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * A exception indicate the user is exists.
 *
 * @author zw.
 */
public class UserExistsException extends ApplicationException {

    private static final long serialVersionUID = 7822368741962546548L;
    
    private final String existedUserName;

    public UserExistsException(String existedUserName) {
        super("The userName [" + existedUserName + "] is already existed.");
        this.existedUserName = existedUserName;
    }

    public String getExistedUserName() {
        return existedUserName;
    }
}
