package com.arcsoft.supervisor.service.message.event;

import com.arcsoft.supervisor.model.domain.message.Message;

/**
 * A event indicate as a remove message.
 *
 * @author zw.
 */
public class RemoveMessageEvent extends MessageEvent {

    /**
     * Constructs a prototypical Event.
     *
     * @param message The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public RemoveMessageEvent(Message message) {
        super(message);
    }
}
