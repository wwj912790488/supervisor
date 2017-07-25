package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.settings.Storage;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author hxiang
 */
@XmlRootElement
public class FindStorageResponse extends BaseResponse {

	private List<Storage> storageList = new ArrayList<Storage>();

	public List<Storage> getStorageList() {
		return storageList;
	}

	public void setStorageList(List<Storage> storageList) {
		this.storageList = storageList;
	}
}
