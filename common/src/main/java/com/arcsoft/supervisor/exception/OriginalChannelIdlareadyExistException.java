package com.arcsoft.supervisor.exception;

/**
 * Created by yshe on 2016/6/17.
 */
@SuppressWarnings("serial")
public class OriginalChannelIdlareadyExistException extends ApplicationException{
    public OriginalChannelIdlareadyExistException(){
        super("The original channel id already exist.");
    }
}
