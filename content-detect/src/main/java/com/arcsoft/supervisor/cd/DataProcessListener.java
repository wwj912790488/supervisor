package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.cd.data.AbstractInfo;

public interface DataProcessListener {
	void receive(int command);

	void receive(AbstractInfo data);
	
	void receivedTaskState(int taskId, int state);
}
