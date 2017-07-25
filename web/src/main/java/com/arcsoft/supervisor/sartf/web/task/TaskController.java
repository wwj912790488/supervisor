package com.arcsoft.supervisor.sartf.web.task;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.task.UserTaskInfo;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import com.arcsoft.supervisor.model.dto.rest.userconfig.PostUserConfigBean;
import com.arcsoft.supervisor.sartf.service.server.SartfOpsServerOperator;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskDispatcherFacade;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskService;
import com.arcsoft.supervisor.sartf.service.user.SartfUserService;
import com.arcsoft.supervisor.sartf.service.user.UserConfigService;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.web.JsonResult;
import com.arcsoft.supervisor.web.task.AbstractTaskController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
@Sartf
public class TaskController extends AbstractTaskController {

	private final SartfTaskService taskService;
	private final SartfTaskDispatcherFacade taskDispatcherFacade;
	private final TaskExecutor taskExecutor;
	private final SartfUserService userService;
	private final UserConfigService userConfigService;
    private final TransactionTemplate transactionTemplate;
    private final SartfOpsServerOperator opsServerOperator;
    private final RtspConfigurationService rtspConfigurationService;

    @Autowired
	protected TaskController(
            SartfTaskDispatcherFacade taskDispatcherFacade,
            ScreenService screenService,
            SartfTaskService taskService,
            TaskExecutor taskExecutor,
            SartfUserService userService,
            UserConfigService userConfigService,
            TransactionTemplate transactionTemplate,
            SartfOpsServerOperator opsServerOperator, RtspConfigurationService rtspConfigurationService) {
		super(taskDispatcherFacade, screenService, taskService);
		this.taskService = taskService;
		this.taskDispatcherFacade = taskDispatcherFacade;
		this.taskExecutor = taskExecutor;
        this.userService = userService;
        this.userConfigService = userConfigService;
		this.transactionTemplate = transactionTemplate;
        this.opsServerOperator = opsServerOperator;
        this.rtspConfigurationService = rtspConfigurationService;
	}

    @RequestMapping(value = "/screen/switchaudiobychannel", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult switchaudiobychannel(Integer channelId, Integer videoSettingId, Integer screenId) {
    	taskService.switchAudioByChannel(taskService.getScreenTask(screenId).getId(),videoSettingId, channelId);
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/reload/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult reloadTask(@PathVariable Integer taskId) {
        taskExecutor.reload(taskId);
        return JsonResult.fromSuccess();
    }


    @RequestMapping(value = "/getusers", method = RequestMethod.GET)
	@ResponseBody
	public List<SartfUser> getUsers() {
		return userService.listAll();
	}

    @RequestMapping(value = "/screen/getcurusertoken_web", method = RequestMethod.GET)
	@ResponseBody
	public JsonResult getCurUserToken( @RequestParam(value = "username") String username) {

    	List<SartfUser> users = userService.listAll();
		for(SartfUser user : users) {
			if(user.getUserName().equals(username)) {
				return JsonResult.fromSuccess()
						.put("token", userService.getTokenById(user.getId()));
			}
		}
		return null;
	}

    @RequestMapping(value = "/screen/setconfig_web", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public boolean setUserConfig(final @RequestBody PostUserConfigBean config) {
    	return transactionTemplate.execute(new TransactionCallback<Boolean>() {
    		@Override
			public Boolean doInTransaction(TransactionStatus status) {

    			String token = config.getToken();
				SartfUser user = userService.getUserByToken(token);
    			if(user == null) {
    				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
    			}
    			Integer id = config.getConfig().getId();
    			if(id == null) {
    				userConfigService.saveUserConfig(user, config.getConfig());
    			} else {
    				UserConfig userconfig = userConfigService.findById(id);
    				if(userconfig == null || userconfig.getUser().getId() != user.getId()) {
    					throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
    				} else {
    					userConfigService.updateUserConfig(userconfig, config.getConfig());
    				}
    			}

    			return true;
    		}
    	});
	}

    @RequestMapping(value = "/screen/startconfig_web", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public HashMap<String, Object> startConfig(String token, Integer config_id, HttpServletRequest request) {

    	HashMap<String, Object> bean = new HashMap<String, Object>();
		Integer userId = userService.getIdByToken(token);
		boolean needUpdate = checkNeedUpdateInTransaction(token, config_id);
		if(needUpdate) {
			userService.updateUserCurrentConfig(userId, config_id);
			taskDispatcherFacade.startUserTask(userId);
		}
		informOpsInTransaction(token);
		bean.put("code", BusinessExceptionDescription.OK.getCode());
		bean.put("url", rtspConfigurationService.composeUrl(getRtspMobileFileNameInTransaction(token), request.getRemoteAddr()));
		return bean;
	}

    private boolean checkNeedUpdateInTransaction(final String token, final Integer config_id) {
		return transactionTemplate.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				SartfUser user = userService.getUserByToken(token);
				UserConfig newConfig = userConfigService.findById(config_id);
				UserConfig oldConfig = user.getCurrent();
				if(newConfig == null) {
					throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
				}
				UserTaskInfo taskinfo = user.getInfo();
				if(taskinfo != null && oldConfig != null && oldConfig.getId() == newConfig.getId() && newConfig.getLastUpdate().before(taskinfo.getLastUpdate())) {
					return false;
				} else {
					return true;
				}
			}
		});
	}

	private void informOpsInTransaction(final String token) {
		transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				SartfUser user = userService.getUserByToken(token);
				SartfOpsServer server = user.getOps();
				UserTaskInfo info = user.getInfo();
				String url = rtspConfigurationService.composeUrl(info.getRtspOpsFileName(), server.getIp());
				opsServerOperator.start(server.getId(), server.getIp(), server.getPort(), url);
				return null;
			}
		});
	}


	private String getRtspMobileFileNameInTransaction(final String token) {
		return transactionTemplate.execute(new TransactionCallback<String>() {

			@Override
			public String doInTransaction(TransactionStatus status) {
				SartfUser user = userService.getUserByToken(token);
				UserTaskInfo info = user.getInfo();
				return info.getRtspMobileFileName();
			}
		});
	}

    @RequestMapping(value = "/screen/stopconfig_web", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
   	@ResponseBody
   	public String stopConfig(final String token) {
    	return transactionTemplate.execute(new TransactionCallback<String>() {
    		@Override
			public String doInTransaction(TransactionStatus status) {

    			Integer userId = userService.getIdByToken(token);
    			taskDispatcherFacade.stopUserTask(userId);
    			return "";
    		}
    	});
   	}



}
