package com.arcsoft.supervisor.recorder.data;

import org.msgpack.annotation.Message;

@Message
public class DirectCommand extends Command {
	
	public int task_id;
	public int direct;
	
	public DirectCommand() {}
	
	public DirectCommand(int taskId, int direct) {
		this.task_id = taskId;
		this.direct = direct;
	}

	@Override
	public int getType() {
		return CommandTypes.COMMAND_TYPE_DIRECT;
	}

	@Override
	public int getTaskId() {
		return task_id;
	}

}
