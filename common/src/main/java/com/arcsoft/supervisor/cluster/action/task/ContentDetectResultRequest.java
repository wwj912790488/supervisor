package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectResult;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author zw.
 */
@XmlRootElement
public class ContentDetectResultRequest extends BaseRequest {

    private ContentDetectResult result;

    public ContentDetectResultRequest() {
    }

    public ContentDetectResultRequest(ContentDetectResult result) {
        this.result = result;
    }

    public ContentDetectResult getResult() {
        return result;
    }

    public void setResult(ContentDetectResult result) {
        this.result = result;
    }
}
