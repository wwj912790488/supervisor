package com.arcsoft.supervisor.recorder.data;

import org.msgpack.annotation.Message;

@Message
public class ReportPortCommand extends Command {
	
	public int task_id;
	public int port;

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getTaskId() {
		return task_id;
	}

}
