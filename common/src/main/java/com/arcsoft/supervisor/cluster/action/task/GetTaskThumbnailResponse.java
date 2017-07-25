package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Response message for {@link GetTaskThumbnailRequest}.
 * 
 * @author zw
 */
@XmlRootElement
public class GetTaskThumbnailResponse extends BaseResponse {
	
	/**
	 * Image as byte array.
	 */
	private byte[] data;

	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	
	
}
