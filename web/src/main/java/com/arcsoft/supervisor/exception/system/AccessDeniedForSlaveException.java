package com.arcsoft.supervisor.exception.system;


import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * This exception indicate this system is a slave, all access will be denied.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class AccessDeniedForSlaveException extends ApplicationException {

}
