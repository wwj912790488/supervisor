package com.arcsoft.supervisor.web.settings;

import com.arcsoft.supervisor.model.domain.system.AlarmConfiguration;
import com.arcsoft.supervisor.model.domain.system.ChannelRecordConfiguration;
import com.arcsoft.supervisor.model.domain.system.GpuConfiguration;
import com.arcsoft.supervisor.model.domain.system.RtspConfiguration;
import com.arcsoft.supervisor.service.channel.ChannelRecordService;
import com.arcsoft.supervisor.service.settings.ConfigurationService;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for configuration.
 *
 * @author zw.
 */
@Controller
@RequestMapping("/cfg")
public class ConfigurationController extends ControllerSupport {

    private final ConfigurationService<RtspConfiguration> rtspConfigurationConfigurationService;
    private final ConfigurationService<GpuConfiguration> gpuConfigurationConfigurationService;
    private final ConfigurationService<ChannelRecordConfiguration> channelRecordConfigurationConfigurationService;
    private final ConfigurationService<AlarmConfiguration> alarmConfigurationConfigurationService;
    private final ChannelRecordService channelRecordService;

    @Autowired
    public ConfigurationController(ConfigurationService<RtspConfiguration> rtspConfigurationConfigurationService,
                                   ConfigurationService<GpuConfiguration> gpuConfigurationConfigurationService,
                                   ConfigurationService<ChannelRecordConfiguration> channelRecordConfigurationConfigurationService,
                                   ConfigurationService<AlarmConfiguration> alarmConfigurationConfigurationService,
                                   ChannelRecordService channelRecordService) {
        this.rtspConfigurationConfigurationService = rtspConfigurationConfigurationService;
        this.gpuConfigurationConfigurationService = gpuConfigurationConfigurationService;
        this.channelRecordConfigurationConfigurationService = channelRecordConfigurationConfigurationService;
        this.alarmConfigurationConfigurationService = alarmConfigurationConfigurationService;
        this.channelRecordService = channelRecordService;
    }

    @RequestMapping(value = "/rtsp", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getRtspConfiguration() {
        return JsonResult.fromSuccess()
                .put(KEY_OF_RESULT, rtspConfigurationConfigurationService.find());
    }

    @RequestMapping(value = "/rtsp", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public JsonResult saveRtspConfiguration(@RequestBody RtspConfiguration cfg) {
        rtspConfigurationConfigurationService.saveOrUpdate(cfg);
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/gpu", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getGpuConfiguration() {
        return JsonResult.fromSuccess().put(KEY_OF_RESULT, gpuConfigurationConfigurationService.find());
    }

    @RequestMapping(value = "/gpu", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public JsonResult saveGpuConfiguration(@RequestBody GpuConfiguration cfg) {
        gpuConfigurationConfigurationService.saveOrUpdate(cfg);
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/c", method = {RequestMethod.GET})
    @ResponseBody
    public JsonResult getCachedConfiguration() {
        Map<String, Object> cachedConfigurations = new HashMap<>();
        cachedConfigurations.put("gpuConfig", gpuConfigurationConfigurationService.getFromCache());
        return JsonResult.fromSuccess()
                .put(KEY_OF_RESULT, cachedConfigurations);
    }

    @RequestMapping(value = "/record", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getRecordConfiguration() {
        ChannelRecordConfiguration cfg = channelRecordConfigurationConfigurationService.find();
        return JsonResult.fromSuccess().put(KEY_OF_RESULT, cfg);
    }

    @RequestMapping(value = "/record", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public JsonResult saveRecordConfiguration(@RequestBody ChannelRecordConfiguration cfg) {
        channelRecordConfigurationConfigurationService.saveOrUpdate(cfg);
        boolean valid = channelRecordService.exist();
        channelRecordService.startRegularFileDeleter();
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/alarm", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getAlarmConfiguration() {
        return JsonResult.fromSuccess()
                .put(KEY_OF_RESULT, alarmConfigurationConfigurationService.find());
    }

    @RequestMapping(value = "/alarm", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public JsonResult saveAlarmConfiguration(@RequestBody AlarmConfiguration cfg) {
        alarmConfigurationConfigurationService.saveOrUpdate(cfg);
        return JsonResult.fromSuccess();
    }
}
