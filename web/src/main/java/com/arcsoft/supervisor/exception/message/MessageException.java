package com.arcsoft.supervisor.exception.message;

import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * A exception indicate the user is exists.
 *
 * @author jt.
 */
public class MessageException extends ApplicationException {

    private static final long serialVersionUID = -8415944039169016688L;
    
    private final String messageException;

    public MessageException(String messageException) {
        super("Post message error.");
        this.messageException = messageException;
    }

    public String getMessageException() {
        return messageException;
    }
}
