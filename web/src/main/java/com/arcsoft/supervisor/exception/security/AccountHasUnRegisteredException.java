package com.arcsoft.supervisor.exception.security;


import com.arcsoft.supervisor.exception.server.AccessDeniedException;

/**
 * Exception that account has unregistered
 * 
 * @author hxiang
 *
 */
@SuppressWarnings("serial")
public class AccountHasUnRegisteredException extends AccessDeniedException {

	public AccountHasUnRegisteredException(String message) {
		super(message);
	}

}
