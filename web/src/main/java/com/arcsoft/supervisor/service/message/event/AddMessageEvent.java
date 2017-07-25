package com.arcsoft.supervisor.service.message.event;

import com.arcsoft.supervisor.model.domain.message.Message;

/**
 * A event indicate as a add message.
 *
 * @author zw.
 */
public class AddMessageEvent extends MessageEvent {

    /**
     * Constructs a prototypical Event.
     *
     * @param message The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public AddMessageEvent(Message message) {
        super(message);
    }
}
