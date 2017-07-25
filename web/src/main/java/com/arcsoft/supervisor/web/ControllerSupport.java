package com.arcsoft.supervisor.web;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.exception.RecordLockedException;
import com.arcsoft.supervisor.exception.server.RemoteException;
import com.arcsoft.supervisor.exception.server.ServerNotAvailableException;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.exception.service.Description;
import com.arcsoft.supervisor.exception.system.SystemNotInitializedException;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.ERROR;

/**
 * Convenient super class for <tt>controller</tt>.
 *
 * @author zw.
 */
public abstract class ControllerSupport {

    /**
     * The key of ajax in http request headers.
     */
    private static final String AJAX_HEADER_KEY = "X-Requested-With";

    /**
     * The value of ajax in http request headers.
     */
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";

    /**
     * The value of key used as json result key.
     */
    public static final String KEY_OF_RESULT = "r";

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * Retrieves {@code User} from session.
     *
     * @param session the http session
     * @return {@code User} existed in session or {@code null} if not in session
     */
    protected User getUserFromSession(HttpSession session) {
        Object userObject = session.getAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO);
        return userObject != null ? (User) userObject : null;
    }


    /**
     * Global exception handler to handle exception of ajax and normal request.This method will doForward {@link Description}
     * from {@code ex} and log the exception.
     *
     * @param ex       the given exception to be handle
     * @param request  the request of currently
     * @param response the response of currently
     * @return {@link ModelAndView}
     * @throws Exception if occurs exception during handle the given {@code ex}
     * @see ControllerSupport#doExceptionHandle(Description, Exception, HttpServletRequest, HttpServletResponse)
     */
    @ExceptionHandler(value = Exception.class)
    private ModelAndView handleException(Exception ex, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ExceptionDescriptionHolder exDesc = convertExceptionDescription(ex);
        logException(exDesc.getEx(), exDesc.getDescription());
        return doExceptionHandle(exDesc.getDescription(), exDesc.getEx(), request, response);
    }

    /**
     * A extended method to do actually exception handle.You can override this method to implementations specific
     * exception handle.
     *
     * @param description the {@link Description} object converted from {@code ex}
     * @param ex          the given exception to be handle
     * @param request     the request of currently
     * @param response    the response of currently
     * @return {@link ModelAndView}
     * @throws Exception if occurs exception during handle the given {@code ex}
     */
    protected ModelAndView doExceptionHandle(Description description, Exception ex, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        if (isAjax(request)) {
            response.setContentType(SupervisorDefs.Constants.PRODUCT_JSON_UTF8);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonMapper.getMapper().writeValueAsString(JsonResult.from(description.getCode())));
            return null;
        } else {
            ModelAndView modelAndView = new ModelAndView("/error");
            modelAndView.addObject(JsonResult.from(description.getCode()).getResult());
            return modelAndView;
        }
    }

    protected void logException(Exception ex, Description description) {
        if (ex instanceof BusinessException) {
            if (ex.getCause() != null) {
                logger.error(description.toString(), ex);
            } else {
                logger.error(description.toString());
            }
        } else {
            logger.error(description.toString(), ex);
        }

    }

    private ExceptionDescriptionHolder convertExceptionDescription(Exception ex) {
        Exception wrapEx = wrapException(ex);
        Description desc = wrapEx instanceof BusinessException ? ((BusinessException) wrapEx).getDescription() : ERROR;
        return new ExceptionDescriptionHolder(wrapEx, desc);
    }

    /**
     * Wrap the convenient exception to {@code BusinessException}.
     *
     * @param ex the exception to be wrapped
     * @return {@code BusinessException} or {@code ex}
     */
    protected Exception wrapException(Exception ex) {
        if (ex instanceof ShellException) {
            return BusinessExceptionDescription.FAILED_EXECUTE_SHELL.withException(ex);
        } else if (ex instanceof RecordLockedException) {
            return BusinessExceptionDescription.RECORD_LOCKED.withException(ex);
        } else if (ex instanceof ServerNotAvailableException) {
            return BusinessExceptionDescription.SERVER_NOT_AVAILABLE.withException(ex);
        } else if (ex instanceof RemoteException) {
            return convertRemoteException((RemoteException) ex);
        } else if (ex instanceof SystemNotInitializedException) {
            return BusinessExceptionDescription.SERVER_SYSTEM_NOT_INITIALIZE.exception();
        }
        return ex;
    }

    /**
     * Converts the given {@code rex} to {@link BusinessException}.
     *
     * @param rex the instance of {@code RemoteException}
     * @return the {@code rex} corresponding to {@code BusinessException}
     */
    protected Exception convertRemoteException(RemoteException rex) {
        switch (rex.getErrorCode()) {
            case ActionErrorCode.STORAGE_NAME_EXISTED:
                return BusinessExceptionDescription.STORAGE_EXISTED.exception();
            default:
                return BusinessExceptionDescription.SERVER_REMOTE_EXCEPTION.withException(rex);
        }
    }


    /**
     * Checks the request is ajax or not.
     *
     * @param request the {@code HttpServletRequest}
     * @return {@code true} if the request is ajax otherwise {@code false}
     */
    protected boolean isAjax(HttpServletRequest request) {
        String xRequestedWith = request.getHeader(AJAX_HEADER_KEY);
        return StringUtils.isNotBlank(xRequestedWith) && AJAX_HEADER_VALUE.equals(xRequestedWith);
    }

    private class ExceptionDescriptionHolder {

        private final Exception ex;

        private final Description description;

        public ExceptionDescriptionHolder(Exception ex, Description description) {
            this.ex = ex;
            this.description = description;
        }

        public Exception getEx() {
            return ex;
        }

        public Description getDescription() {
            return description;
        }
    }


}
