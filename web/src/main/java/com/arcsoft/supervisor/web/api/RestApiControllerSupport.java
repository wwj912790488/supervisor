package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.commons.freemarker.FreemarkerService;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.exception.service.Description;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A abstract class defines some convenient method for rest api.
 *
 * @author zw.
 */
public abstract class RestApiControllerSupport extends ControllerSupport {

    @Autowired
    protected FreemarkerService freemarkerService;

    /**
     * Retrieves the json string with specify {@code resultCode}.
     *
     * @param description {@link BusinessExceptionDescription}. the business exception description
     * @return the string contains the {@code code}
     */
    protected String renderResponseCodeJson(Description description) {
        return String.format("{\"code\" : %1d}", description.getCode());
    }

    protected String renderResponseCodeJson(Integer code) {
        return String.format("{\"code\" : %1d}", code);
    }

    /**
     * Retrieves the success json string.
     *
     * @return the string contains a success's code value
     */
    protected String renderSuccessResponse() {
        return renderResponseCodeJson(BusinessExceptionDescription.OK);
    }

    /**
     * Retrieves the error json string.
     *
     * @return the string contains a error's code value
     */
    protected String renderErrorResponse() {
        return renderResponseCodeJson(BusinessExceptionDescription.ERROR);
    }

    /**
     * Retrieves the empty json string.
     *
     * @return the string contains a empty's code value
     */
    public String renderEmptyResponse() {
        return renderResponseCodeJson(BusinessExceptionDescription.INVALID_ARGUMENTS);
    }

    @Override
    protected ModelAndView doExceptionHandle(Description description, Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(SupervisorDefs.Constants.PRODUCT_JSON_UTF8);
        response.getWriter().write(JsonMapper.getMapper().writeValueAsString(JsonResult.from(description.getCode())));
        return null;
    }

}
