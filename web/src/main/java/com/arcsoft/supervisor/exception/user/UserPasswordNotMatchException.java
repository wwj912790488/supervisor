package com.arcsoft.supervisor.exception.user;

import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * A exception indicate the user is exists.
 *
 * @author zw.
 */
public class UserPasswordNotMatchException extends ApplicationException {


    private static final long serialVersionUID = 116734899134707199L;

    public UserPasswordNotMatchException() {
        super("Password not match.");
    }
}