package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


/**
 * 
 * Send a request to generate files md5 info of specified task id.
 * 
 * @author zw
 */
@XmlRootElement
public class Md5sumRequest extends BaseRequest {
	
	private List<String> filePaths;

	
	public List<String> getFilePaths() {
		return filePaths;
	}

	
	public void setFilePaths(List<String> filePaths) {
		this.filePaths = filePaths;
	}

}
