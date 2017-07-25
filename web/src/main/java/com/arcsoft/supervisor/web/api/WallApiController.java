package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.commons.freemarker.FreemarkerService;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.exception.WallNotExistsException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.dto.rest.wall.RootWallBean;
import com.arcsoft.supervisor.service.graphic.WallService;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.OK;
/**
 * Controller class for rest api of {@code wall}.
 *
 * @author zw.
 */
@Controller
public class WallApiController extends RestApiControllerSupport {

    @Autowired
    private WallService wallService;

    @Autowired
    private FreemarkerService freemarkerService;

    @RequestMapping(method = RequestMethod.GET, value = "/getwalll_app", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getWalls(String token) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", OK.getCode());
        model.put("walls", wallService.findAll());
        return freemarkerService.renderFromTemplateFile("wall.ftl", model);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setwallls_app", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateWall(@RequestBody String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return renderEmptyResponse();
        }
        RootWallBean rootWallBean;
        try {
            rootWallBean = JsonMapper.getMapper().readValue(jsonString, RootWallBean.class);
        } catch (IOException e) {
            throw BusinessExceptionDescription.CONVERT_INPUT_ARGUMENTS_FAILED.exception();
        }
        if (rootWallBean == null) {
            throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
        }
        try {
            wallService.switchWallPosition(rootWallBean);
            return renderSuccessResponse();
        } catch (WallNotExistsException e) {
            throw BusinessExceptionDescription.WALL_NOT_EXISTS.exception();
        }
    }
}
