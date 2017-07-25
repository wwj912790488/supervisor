package com.arcsoft.supervisor.exception.user;

import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * A exception indicate the user is exists.
 *
 * @author zw.
 */
public class TokenExpireException extends ApplicationException {

    //private static final long serialVersionUID = 7822368741962546548L;
    
    public TokenExpireException() {
        super("The token is expired.");
    }


}
