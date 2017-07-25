package com.arcsoft.supervisor.web.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;

import com.arcsoft.supervisor.model.domain.alarm.AlarmConfig;
import com.arcsoft.supervisor.model.domain.alarm.AlarmDevice;
import com.arcsoft.supervisor.model.domain.alarm.AlarmPushLog;
import com.arcsoft.supervisor.model.dto.rest.alarmconfig.AlarmConfigSetBean;
import com.arcsoft.supervisor.model.dto.rest.alarmconfig.PostAlarmConfigBean;
import com.arcsoft.supervisor.service.alarm.AlarmBaiduPushService;
import com.arcsoft.supervisor.service.alarm.AlarmConfigService;
import com.arcsoft.supervisor.service.alarm.impl.DefaultAlarmDeviceService;
import com.arcsoft.supervisor.service.converter.impl.AlarmConfigAndCommonAlarmConfigBeanConverter;
import com.arcsoft.supervisor.service.alarm.impl.CustomAlarmLogQueryParams;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.channel.ChannelGroupService;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.log.impl.ChannelsContentDetectQueryParams;
import com.arcsoft.supervisor.service.user.UserService;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import freemarker.template.TemplateException;



/**
 * a class for alarm rest api
 *
 * @author jt.
 */
@Controller
public class AlarmApiController extends RestApiControllerSupport {

    @Autowired
    private UserService<User> userService;
    
    @Autowired
    private ChannelService channelService;
    
    @Autowired
    private ChannelGroupService channelGroupService;
    
    @Autowired
    private ContentDetectLogService contentDetectLogService;

    @Autowired
    private DefaultAlarmDeviceService alarmDeviceService;

    @Autowired
    private AlarmConfigService alarmConfigService;

    @Autowired
    private AlarmConfigAndCommonAlarmConfigBeanConverter alarmConfigConverter;

    @Autowired
    private AlarmBaiduPushService alarmBaiduPushService;

    private Integer defaultQueryMins = 30;




    @RequestMapping(value = "/alarmlogin_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String login(@RequestParam(value = "username", required = true) String userName, String password, 
    		String channelid, String devicetype) 
    				throws IOException, TemplateException {
        check(userName, password,channelid,devicetype);
        User user = userService.login(userName, password,true);
        if (user == null) {
            return renderResponseCodeJson(BusinessExceptionDescription.USER_LOGIN_NAME_OR_PASSWORD_INCORRECT);
        }

        AlarmDevice alarmDev = alarmDeviceService.findDevByChannelId(channelid);
        if(alarmDev == null)
        {
            alarmDev = new AlarmDevice(channelid,devicetype,user);
            alarmDeviceService.save(alarmDev);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", BusinessExceptionDescription.OK.getCode());
        model.put("user", user);
        return freemarkerService.renderFromTemplateFile("alarmlogin.ftl", model);
    }

    private void check(String userName, String password,String channelid, String devicetype) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)
        		||StringUtils.isBlank(channelid) || StringUtils.isBlank(devicetype)) {
            throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
        }
    }
    
    @RequestMapping(value = "/alarmlogout_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String loginOut(String token) {
    	Integer userId = userService.getIdByToken(token);
		userService.logout(userId);
        return renderSuccessResponse();
    }
    
    @RequestMapping(value = "/alarmgrouplist_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listgroup(String token)
            throws IOException, TemplateException {
        if (StringUtils.isBlank(token)) {
            return renderEmptyResponse();
        }
        List<ChannelGroup> groups = channelGroupService.listAll();
        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", BusinessExceptionDescription.OK.getCode());
        model.put("groupObj", groups);
        return freemarkerService.renderFromTemplateFile("alarmgroup.ftl", model);
    }
    
    @RequestMapping(value = "/alarmchannellist_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listchannel(@RequestParam(value = "groupid", required = false) Integer groupid, String token)
            throws IOException, TemplateException {
        if (StringUtils.isBlank(token)) {
            return renderEmptyResponse();
        }
        groupid = (groupid == null || groupid <= 0) ? 0 : groupid;
        List<Channel> channels =  groupid == 0 ? channelService.getUngrouped():channelService.getByGroupId(groupid);

        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", BusinessExceptionDescription.OK.getCode());
        model.put("groupId",groupid);
        model.put("channelObj", channels);
        return freemarkerService.renderFromTemplateFile("alarmchannel.ftl", model);
    }
    
    @RequestMapping(value = "/getchannelalarminfo_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String channelAlarmInfo(@RequestParam(value = "chid", required = false) Integer chid,
    		String token, Long starttime, Long endtime)
            throws IOException, TemplateException {
        if (StringUtils.isBlank(token)) {
            return renderEmptyResponse();
        }
        //initial query params
        ChannelsContentDetectQueryParams params = new ChannelsContentDetectQueryParams();
        Date queryStartTime = new Date();
        Date queryEndTime;
        if(starttime == null || starttime == 0){
        	queryStartTime = new Date(queryStartTime.getTime() - 1000*60*defaultQueryMins);// 30mins
        }else{
        	queryStartTime = new Date(starttime);
        }
        if(endtime == null || endtime == 0){
            queryEndTime = new Date();
        }else{
            queryEndTime = new Date(endtime);
        }
        LocalDateTime start = new LocalDateTime(queryStartTime, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        LocalDateTime end = new LocalDateTime(queryEndTime, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        queryStartTime = start.toDate(TimeZone.getDefault());
        queryEndTime = end.toDate(TimeZone.getDefault());
        params.setStartTime(queryStartTime);
        params.setEndTime(queryEndTime);
        List<Integer> channelList = new ArrayList<Integer>();
        channelList.add(chid);
        params.setChannelIds(channelList);
        List<ContentDetectLog> logs = contentDetectLogService.findByAllType(params);
        for(ContentDetectLog log : logs)
        {
            if(log.getStartTime()!=0){
                log.setStartTimeAsDate(new Date(log.getStartTime()));
            }
            if(log.getEndTime()!= 0){
                log.setEndTimeAsDate(new Date((log.getEndTime())));
            }
        }

        Map<String, Object> results = new HashMap<>();
        results.put("statusCode", BusinessExceptionDescription.OK.getCode());
        results.put("logobjs", logs);
        results.put("start", queryStartTime.getTime());
        results.put("end", queryEndTime.getTime());

        return freemarkerService.renderFromTemplateFile("alarmchannellogs.ftl", results);
    }
    
    @RequestMapping(value = "/getcustomalarminfo_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getCustomAlarmInfo(String token, Long starttime, Long endtime)
            throws IOException, TemplateException {
        if (StringUtils.isBlank(token)) {
            return renderEmptyResponse();
        }
        //initial query params
        CustomAlarmLogQueryParams params = new CustomAlarmLogQueryParams();
        Date queryStartTime = new Date();
        Date queryEndTime;
        if(starttime == null || starttime == 0){
            queryStartTime = new Date(queryStartTime.getTime() - 1000*60*defaultQueryMins);// 30mins
        }else{
            queryStartTime = new Date(starttime);
        }
        if(endtime == null || endtime == 0){
            queryEndTime = new Date();
        }else{
            queryEndTime = new Date(endtime);
        }
        LocalDateTime start = new LocalDateTime(queryStartTime, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        LocalDateTime end = new LocalDateTime(queryEndTime, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        queryStartTime = start.toDate(TimeZone.getDefault());
        queryEndTime = end.toDate(TimeZone.getDefault());
        params.setStartTime(queryStartTime);
        params.setEndTime(queryEndTime);
        User user = userService.getUserByToken(token);
        params.setConfigId(user.getAlarmConfig().getId());
        List<AlarmPushLog> logs = alarmBaiduPushService.findAll(params);

        Map<String, Object> results = new HashMap<>();
        results.put("statusCode", BusinessExceptionDescription.OK.getCode());
        results.put("logobjs", logs);
        results.put("start", queryStartTime.getTime());
        results.put("end", queryEndTime.getTime());

        return freemarkerService.renderFromTemplateFile("alarmchannellogs.ftl", results);
    }

    @RequestMapping(value = "/getalarmconfig_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public AlarmConfigSetBean getAlarmConfig(String token) throws Exception {
		AlarmConfigSetBean bean = new AlarmConfigSetBean();
		User user = userService.getUserByToken(token);
		if(user == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}
        if(user.getAlarmConfig() == null){
            AlarmConfig config = new AlarmConfig();
            config.setDefault();
            bean.setConfig(alarmConfigConverter.doForward(config));
            bean.setCode(BusinessExceptionDescription.OK.getCode());
        }else{
            bean.setConfig(alarmConfigConverter.doForward(user.getAlarmConfig()));
            bean.setCode(BusinessExceptionDescription.OK.getCode());
        }
		return bean;
	}
	
	@RequestMapping(value = "/setalarmconfig_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String setAlarmConfig(@RequestBody PostAlarmConfigBean config) {
		String token = config.getToken();
		User user = userService.getUserByToken(token);
		if(user == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}
		Integer id = config.getConfig().getId();
        if(id == null && user.getAlarmConfig() != null) {
            id = user.getAlarmConfig().getId();
        }
		if(id == null) {
			alarmConfigService.saveAlarmConfig(user, config.getConfig());
		} else {
			AlarmConfig alarmConfig = alarmConfigService.findById(id);
			if(alarmConfig == null || alarmConfig.getUser().getId() != user.getId()) {
				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
			} else {
                alarmConfigService.updateAlarmConfig(alarmConfig, config.getConfig());
			}
		}
		return renderSuccessResponse();
	}
}
