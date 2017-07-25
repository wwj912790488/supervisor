package com.arcsoft.supervisor.cluster.action.task;


import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.vo.task.TaskStateChange;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Notify commander that the specified task state changes.
 * 
 * @author fjli
 */
@XmlRootElement
public class StateChangeRequest extends BaseRequest {
	
	private List<TaskStateChange> states;

	public List<TaskStateChange> getStates() {
		return states;
	}
	
	public void setStates(List<TaskStateChange> states) {
		this.states = states;
	}
	
	public void add(TaskStateChange state){
		if(this.states == null){
			this.states = new ArrayList<>();
		}
		this.states.add(state);
	}

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StateChangeRequest{");
        sb.append("states=").append(states);
        sb.append('}');
        return sb.toString();
    }
}
