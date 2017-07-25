package com.arcsoft.supervisor.recorder.data;

import org.msgpack.annotation.Message;

@Message
// Annotation
public abstract class Command {
	public abstract int getType();
	public abstract int getTaskId();
}
