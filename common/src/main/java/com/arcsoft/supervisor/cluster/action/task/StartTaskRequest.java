package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Request the specified server to start the specified task.
 * 
 * @author zw
 */
@XmlRootElement
public class StartTaskRequest extends BaseRequest {

	private List<Integer> ids;
	private List<String> datas;
	/**This property used for M+N switch.The value can let transcoder to know current task is switch from other server.
	 * <br>more info see:<br>
	 * {@link TranscodingParams#OUTPUT_OPTION_OUPUT_BACKUP_N}
	 **/
	private Integer outputOption;

	public List<Integer> getIds() {
		return ids;
	}
	
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
	
	public List<String> getDatas() {
		return datas;
	}
	
	public void setDatas(List<String> datas) {
		this.datas = datas;
	}
	
	public Integer getOutputOption() {
		return outputOption;
	}

	public void setOutputOption(Integer outputOption) {
		this.outputOption = outputOption;
	}

	
}
