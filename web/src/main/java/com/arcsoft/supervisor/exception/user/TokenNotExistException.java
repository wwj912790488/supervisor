package com.arcsoft.supervisor.exception.user;

import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * A exception indicate the user is exists.
 *
 * @author zw.
 */
public class TokenNotExistException extends ApplicationException {

    //private static final long serialVersionUID = 7822368741962546548L;

    public TokenNotExistException() {
        super("The token is invalid.");
    }

}
