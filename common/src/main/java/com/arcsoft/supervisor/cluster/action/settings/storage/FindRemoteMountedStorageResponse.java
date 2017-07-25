package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;


/**
 * The response used to find all storages which is remote and has mounted.
 * 
 * @author hxiang
 */
@XmlRootElement
public class FindRemoteMountedStorageResponse extends BaseResponse {

	Map<String, String> storageMap = new HashMap<String, String>();
	
	public Map<String, String> getStorageMap() {
		return storageMap;
	}
	
	public void setStorageMap(Map<String, String> storageMap) {
		this.storageMap = storageMap;
	}
}
