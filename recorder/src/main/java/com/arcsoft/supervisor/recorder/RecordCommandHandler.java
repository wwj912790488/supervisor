package com.arcsoft.supervisor.recorder;

import com.arcsoft.supervisor.recorder.data.Command;

public interface RecordCommandHandler {
	void receive(Command comand);
}
