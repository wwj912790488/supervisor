package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.settings.Storage;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * @author hxiang
 */
@XmlRootElement
public class AddStorageResponse extends BaseResponse {

	private Storage storage = null;

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public Storage getStorage(){
		return this.storage;
	}
}
