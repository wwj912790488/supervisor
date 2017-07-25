package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.commons.spring.SpringUtils;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelMobileConfig;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.TaskService;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.OK;


/**
 * Controller class for rest api of module {@code channel}.
 *
 * @author zw.
 */
@Api(value = "频道", description = "关于频道的CURD操作")
@Controller
public class ChannelApiController extends RestApiControllerSupport {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private RtspConfigurationService rtspConfigurationService;

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/api-doc", method = RequestMethod.GET)
    public String apiDoc() {
        return "redirect:swagger-ui.html";
    }

   /* @RequestMapping(value = "/api-doc", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void apiDoc() {
        String snippetDir = "target/generated-snippets";
        String outputDir = "target/asciidoc";
        //private String indexDoc = "docs/asciidoc/index.adoc";
        // 得到swagger.json,写入outputDir目录中
        try {
            get("/v2/api-docs").accept(MediaType.APPLICATION_JSON);
            SwaggerResultHandler.outputDirectory(outputDir).build();

            // 读取上一步生成的swagger.json转成asciiDoc,写入到outputDir
            // 这个outputDir必须和插件里面<generated></generated>标签配置一致
            Swagger2MarkupConverter.from(outputDir + "/swagger.json")
                    .withPathsGroupedBy(GroupBy.TAGS)// 按tag排序
                    .withMarkupLanguage(MarkupLanguage.ASCIIDOC)// 格式
                    .withExamples(snippetDir)
                    .build()
                    .intoFolder(outputDir);// 输出
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/

    @ApiOperation(value = "获取频道列表", notes = "根据url获取频道列表信息")
    @RequestMapping(value = "/channellist_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String list(@RequestParam(value = "pageno", required = false) Integer pageNo,
                       @ApiParam(required = true, name = "token", value = "令牌") @RequestParam(value = "token") String token, HttpServletRequest request)
            throws IOException, TemplateException {
        if (StringUtils.isBlank(token)) {
            return renderEmptyResponse();
        }
        pageNo = (pageNo == null || pageNo <= 0) ? 0 : (pageNo - 1);
        Page<Channel> channels = channelService.pagenate(pageNo, SupervisorDefs.Constants.PAGE_SIZE);
        setRtspUrl(channels, request.getRemoteAddr());
        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", OK.getCode());
        model.put("pageObject", channels);
        return freemarkerService.renderFromTemplateFile("channel.ftl", model);
    }


    private void setRtspUrl(Page<Channel> channels, String clientIp) {
        if (channels != null) {
            for (Channel channel : channels.getContent()) {
                if (channel.getIsSupportMobile() && channel.getMobileConfigs() != null && !channel.getMobileConfigs().isEmpty()) {
                    for (ChannelMobileConfig config : channel.getMobileConfigs()) {
                        if (StringUtils.isNotBlank(config.getAddress())) {
                            String url = rtspConfigurationService.composeUrl(config.getAddress(), clientIp);
                            if (config.getType() == 0) {
                                channel.setSdUrl(url);
                            } else {
                                channel.setHdUrl(url);
                            }
                        }
                    }
                }
            }
        }
    }

    //@ApiOperation(value = "获取用户列表", notes = "")
    @ApiIgnore
    @RequestMapping(value = "/getchannel_url_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> getChannelUrl(@ApiParam(required = true, name = "id", value = "频道id") @RequestParam(value = "id") Integer id) {
        HashMap<String, Object> result = new HashMap<>();
        Channel channel = channelService.getById(id);
        if (channel != null) {
            String clientIp = SpringUtils.getThreadBoundedHttpServletRequest().getRemoteAddr();
            boolean bSupportMobile = false;
            try {
                if (channel.getIsSupportMobile() && channel.getMobileConfigs() != null && !channel.getMobileConfigs().isEmpty()) {
                    bSupportMobile = true;
                }
            } catch (Exception e) {
                bSupportMobile = false;
            }
            if (bSupportMobile) {
                //check channel task running status
                boolean isrunning = false;
                try {
                    Task rtspTask = taskService.getByTypeAndReferenceId(channel.getId(), TaskType.RTSP);
                    if (rtspTask != null && rtspTask.isStatusEqual(TaskStatus.RUNNING)) {
                        isrunning = true;
                    }
                } catch (Exception e) {
                    isrunning = false;
                }

                if (isrunning) {
                    result.put("code", OK.getCode());
                    result.put("url", "");
                    for (ChannelMobileConfig config : channel.getMobileConfigs()) {
                        if (StringUtils.isNotBlank(config.getAddress())) {
                            String url = rtspConfigurationService.composeUrl(config.getAddress(), clientIp);
                            if (config.getType() != 0) {
                                result.put("url", url);
                            } /*else {
                            result.put("url",url);
                        }*/
                        }
                    }
                } else {
                    result.put("code", BusinessExceptionDescription.TASK_NOT_RUNNING.getCode());//channel task is not running
                }
            } else {
                result.put("code", BusinessExceptionDescription.CHANNEL_NOT_SUPPORT_MOBILE.getCode());//channel does not support mobile
            }
        } else {
            result.put("code", BusinessExceptionDescription.CHANNEL_NOT_EXIST.getCode());//id is not exist
        }

        return result;
    }
}
