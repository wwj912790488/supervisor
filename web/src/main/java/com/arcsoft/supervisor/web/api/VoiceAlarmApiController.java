package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yshe on 2016/4/13.
 */
@Controller
public class VoiceAlarmApiController extends RestApiControllerSupport{
    @Autowired
    private ContentDetectLogService contentDetectLogService;

    @RequestMapping(value = "/alarm_confirm_app/{id}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String confirmLog(@PathVariable(value = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return renderResponseCodeJson(BusinessExceptionDescription.INVALID_ARGUMENTS );
        }

        Integer logid;
        try{
            logid = Integer.valueOf(id);
        }catch (Exception e)
        {
            return renderResponseCodeJson(BusinessExceptionDescription.INVALID_ARGUMENTS );
        }

        return renderResponseCodeJson(contentDetectLogService.updateConfirmDate(logid) );
    }
}
