package com.arcsoft.supervisor.utils.app;

/**
 * Exception for linux shell.
 *
 * @author hxiang
 */
@SuppressWarnings("serial")
public class ShellException extends Exception{
	
	public ShellException(String strError){
		super(strError);
	}
	
	public ShellException(Throwable able){
		super(able);
	}
	
	public ShellException(String message, Throwable cause) {
        super(message, cause);
    }
}
