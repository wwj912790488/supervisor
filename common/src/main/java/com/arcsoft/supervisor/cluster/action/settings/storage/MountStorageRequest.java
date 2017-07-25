package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.domain.settings.Storage;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request for mount storage
 * 
 * @author hxiang
 */
@XmlRootElement
public class MountStorageRequest extends BaseRequest {

	private Storage storage;

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public Storage getStorage() {
		return storage;
	}

}
