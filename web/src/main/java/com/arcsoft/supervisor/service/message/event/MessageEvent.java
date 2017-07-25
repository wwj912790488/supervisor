package com.arcsoft.supervisor.service.message.event;

import com.arcsoft.supervisor.model.domain.message.Message;

import java.util.EventObject;

/**
 * A event class for <code>Message</code>.
 *
 * @author zw.
 */
public class MessageEvent extends EventObject {

    private final Message message;

    /**
     * Constructs a prototypical Event.
     *
     * @param message The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public MessageEvent(Message message) {
        super(message);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
