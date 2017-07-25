package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.domain.settings.Storage;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Request for unmount storage
 * 
 * @author hxiang
 */
@XmlRootElement
public class UnmountStorageRequest extends BaseRequest {

	private Storage storage;
	public Storage getStorage() {
		return storage;
	}
	
	public void setStorage(Storage storage) {
		this.storage = storage;
	}

}
