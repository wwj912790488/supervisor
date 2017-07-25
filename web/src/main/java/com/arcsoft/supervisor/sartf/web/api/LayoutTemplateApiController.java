package com.arcsoft.supervisor.sartf.web.api;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import com.arcsoft.supervisor.model.dto.rest.layouttemplate.UpdatedLayoutTemplate;
import com.arcsoft.supervisor.sartf.service.layouttemplate.LayoutTemplateService;
import com.arcsoft.supervisor.web.api.RestApiControllerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@Sartf
public class LayoutTemplateApiController extends RestApiControllerSupport {

	private final LayoutTemplateService layoutTemplateService;

	@Autowired
	public LayoutTemplateApiController(LayoutTemplateService layoutTemplateService) {
		this.layoutTemplateService = layoutTemplateService;
	}

    public LayoutTemplateService getLayoutTemplateService() {
        return layoutTemplateService;
    }

    @RequestMapping(value = "/getlayouttemplates_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public UpdatedLayoutTemplate updateLayouTemplate(String lastupdate, String token) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		try {
			List<LayoutTemplate> templates;
			if(lastupdate == null) {
				templates = layoutTemplateService.findAll();
			} else {
				Date date = sf.parse(lastupdate);
				templates = layoutTemplateService.getUpdated(date);
			}
			UpdatedLayoutTemplate updatedLayoutTemplate = new UpdatedLayoutTemplate();
			updatedLayoutTemplate.setCode(BusinessExceptionDescription.OK.getCode());
			updatedLayoutTemplate.setTemplates(templates);
			updatedLayoutTemplate.setUpdatedDate(new Date());
			return updatedLayoutTemplate;
		} catch (ParseException e) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.withException(e);
		}	
	}
}
