package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.domain.settings.Storage;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author hxiang
 */
@XmlRootElement
public class AddStorageRequest extends BaseRequest {

	private Storage storage;

	
	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public Storage getStorage() {
		return storage;
	}

}
