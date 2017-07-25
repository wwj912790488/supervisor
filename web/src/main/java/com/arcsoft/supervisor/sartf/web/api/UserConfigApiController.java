package com.arcsoft.supervisor.sartf.web.api;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelMobileConfig;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateCell;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.task.UserTaskInfo;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfigChannel;
import com.arcsoft.supervisor.model.dto.rest.userconfig.AudioChannelBean;
import com.arcsoft.supervisor.model.dto.rest.userconfig.PostUserConfigBean;
import com.arcsoft.supervisor.model.dto.rest.userconfig.UserConfigSetBean;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.sartf.repository.user.UserConfigRepository;
import com.arcsoft.supervisor.sartf.service.server.SartfOpsServerOperator;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskDispatcherFacade;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskService;
import com.arcsoft.supervisor.sartf.service.user.SartfUserService;
import com.arcsoft.supervisor.sartf.service.user.UserConfigService;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.web.api.RestApiControllerSupport;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.INVALID_ARGUMENTS;

@Controller
@Sartf
public class UserConfigApiController extends RestApiControllerSupport {

	private static final int MAX_WAIT_OUTPUT_TIMES = 5;

	private final SartfUserService userService;

	private final UserConfigRepository userconfigRepository;

    private final SartfTaskDispatcherFacade taskDispatcherFacade;

	private final UserConfigService userConfigService;

    private final TransactionTemplate transactionTemplate;

    private final SartfOpsServerOperator opsServerOperator;

    private final ChannelService channelService;

	private final RtspConfigurationService rtspConfigurationService;

	private final SartfTaskService taskService;

    @Autowired
	public UserConfigApiController(
			SartfUserService userService,
			UserConfigRepository userconfigRepository,
			SartfTaskDispatcherFacade taskDispatcherFacade,
			UserConfigService userConfigService,
			TransactionTemplate transactionTemplate,
			SartfOpsServerOperator opsServerOperator,
			ChannelService channelService,
			RtspConfigurationService rtspConfigurationService,
			SartfTaskService taskService) {
		this.userService = userService;
		this.userconfigRepository = userconfigRepository;
		this.taskDispatcherFacade = taskDispatcherFacade;
		this.userConfigService = userConfigService;
		this.transactionTemplate = transactionTemplate;
		this.opsServerOperator = opsServerOperator;
		this.channelService = channelService;
		this.rtspConfigurationService = rtspConfigurationService;
		this.taskService = taskService;
	}

    public SartfUserService getUserService() {
        return userService;
    }

    public UserConfigRepository getUserconfigRepository() {
        return userconfigRepository;
    }

    public SartfTaskDispatcherFacade getTaskDispatcherFacade() {
        return taskDispatcherFacade;
    }

    public UserConfigService getUserConfigService() {
        return userConfigService;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public SartfOpsServerOperator getOpsServerOperator() {
        return opsServerOperator;
    }

    public ChannelService getChannelService() {
        return channelService;
    }

    public RtspConfigurationService getRtspConfigurationService() {
        return rtspConfigurationService;
    }

    public SartfTaskService getTaskService() {
        return taskService;
    }

    @RequestMapping(value = "/getconfig_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public UserConfigSetBean getUserConfig(String token) {
		UserConfigSetBean bean = new UserConfigSetBean();
		SartfUser user = userService.getUserByToken(token);
		if(user == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}
		bean.setConfigs(user.getConfigs());
		UserConfig current = user.getCurrent();
		bean.setRunning_id(current == null ? -1 : current.getId());
		bean.setCode(BusinessExceptionDescription.OK.getCode());
		return bean;
	}

	@RequestMapping(value = "/setconfig_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String setUserConfig(@RequestBody PostUserConfigBean config) {
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

		return renderSuccessResponse();
	}

	@RequestMapping(value = "/getconfigaudio_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public AudioChannelBean getUserConfigAudioChannel(String token, Integer config_id) {
		if(config_id == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}
		UserConfig userconfig = userConfigService.findById(config_id);
		if(userconfig == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}

		AudioChannelBean bean = new AudioChannelBean();
		LayoutTemplateCell audioCell = userconfig.getCell();
		bean.setAudio_cell_index(audioCell == null ? -1 : audioCell.getCell_index());
		bean.setCode(BusinessExceptionDescription.OK.getCode());
		return bean;
	}

	@RequestMapping(value = "/setconfigaudio_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String setUserConfigAudioChannel(@RequestBody AudioChannelBean bean) {
		Integer config_id = bean.getConfig_id();
		Integer cell_index = bean.getAudio_cell_index();
		String token = bean.getToken();

		userConfigService.updateUserConfigAudioCellIndex(config_id, cell_index);

		SartfUser user = userService.getUserByToken(token);
		UserConfig current = user.getCurrent();

		if(current != null && current.getId() == config_id) {
			int actual_cell_index = -1;
			if(cell_index == -1) {
				actual_cell_index = 99;
			} else {
				for(UserConfigChannel channel : current.getChannels()) {
					if(channel.getCell().getCell_index() == cell_index) {
						actual_cell_index = cell_index;
					}
				}
			}
			if(actual_cell_index != -1) {
				taskDispatcherFacade.switchAudioChannel(user.getId(), actual_cell_index);
			} else {
				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
			}

		}
		return renderSuccessResponse();
	}

	@RequestMapping(value = "/startconfig_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public HashMap<String, Object> startConfig(String token, Integer config_id, HttpServletRequest request) {
		HashMap<String, Object> bean = new HashMap<String, Object>();
		Integer userId = userService.getIdByToken(token);
		Integer needUpdateStatus = checkNeedUpdateInTransaction(token, config_id);
		if(needUpdateStatus == -1) {
			throw BusinessExceptionDescription.USER_CONFIG_INCOMPLETE.exception();
		} else if(needUpdateStatus == 1) {
			userService.updateUserCurrentConfig(userId, config_id);
			taskDispatcherFacade.startUserTask(userId);
			//waitForStreamOutput(userId);
			switchAudioChannelInTransaction(token);
		} else if(needUpdateStatus == 2) {
			userService.updateUserCurrentConfig(userId, config_id);
			taskDispatcherFacade.startUserTask(userId);
			waitForStreamOutput(userId);
			switchAudioChannelInTransaction(token);
		}
		informOpsInTransaction(token);
		bean.put("code", BusinessExceptionDescription.OK.getCode());
		bean.put("url", rtspConfigurationService.composeUrl(getRtspMobileFileNameInTransaction(token), request.getRemoteAddr()));
		return bean;
	}

	private void waitForStreamOutput(int userId) {
        int times = 0;
        while (MAX_WAIT_OUTPUT_TIMES > times
                && !taskService.isUserRelatedTaskHasOutput(userId)) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            times++;
        }
    }

	private void switchAudioChannelInTransaction(final String token) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				SartfUser user = userService.getUserByToken(token);
				UserConfig current = user.getCurrent();
				if(current != null) {
					int cell_index = 99;
					if(current.getCell() != null) {
						cell_index = current.getCell().getCell_index();
					}
					taskDispatcherFacade.switchAudioChannel(user.getId(), cell_index);
				}
				return null;

			}

		});

	}

	@RequestMapping(value = "/stopconfig_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String stopConfig(String token) {
		Integer userId = userService.getIdByToken(token);
		taskDispatcherFacade.stopUserTask(userId);
		return renderSuccessResponse();
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

	private Integer checkNeedUpdateInTransaction(final String token, final Integer config_id) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				SartfUser user = userService.getUserByToken(token);
				UserConfig newConfig = userConfigService.findById(config_id);
				UserConfig oldConfig = user.getCurrent();
				if(user == null || newConfig == null) {
					throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
				}
				UserTaskInfo taskinfo = user.getInfo();
				if(taskinfo == null || !taskinfo.getTask().isStatusEqual(TaskStatus.RUNNING)) {
					return 1; //task is not running, run task.
				} else if(oldConfig != null && oldConfig.getId() == newConfig.getId() && newConfig.getLastUpdate().before(taskinfo.getLastUpdate())) {
						return 0;//config not changed and task is running, do nothing.
				} else if(newConfig.getTemplate() == null) {
					return -1; //incomplete config, error
				} else {
					return 2;//config changed, set config and run task;
				}
			}

		});
	}


	@RequestMapping(value = "/startchannel_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> startchannel(String token,Integer channel_id, Integer notify, HttpServletRequest request)
            throws IOException, TemplateException {

		check(token, channel_id);
		SartfUser user = userService.getUserByToken(token);
		if(user == null)
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();

		SartfOpsServer server = user.getOps();
		if(server == null)
			throw BusinessExceptionDescription.TASK_USER_OPS_NOT_BIND.exception();

		Channel channel = channelService.getById(channel_id);
		if(channel == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}

		List<ChannelMobileConfig> configs = channel.getMobileConfigs();
		String hdUrl = null;
		for(ChannelMobileConfig config : configs) {
			if(config.getType() == 1) {
				hdUrl = config.getAddress();
			}
		}

		if(hdUrl == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}

		if(notify != null && notify == 1) {

			String opsRtspUrl = rtspConfigurationService.composeUrl(hdUrl, server.getIp());

			//opsServerOperator.start(server, rtspUrl);
			opsServerOperator.start(server.getId(), server.getIp(), server.getPort(), opsRtspUrl);
		}

		String mobileRtspUrl = rtspConfigurationService.composeUrl(hdUrl, request.getRemoteAddr());
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("code", 0);
		result.put("url", mobileRtspUrl);
		return result;
    }

    private void check(String token, Integer channel_id) {
        if (StringUtils.isBlank(token)) {
            throw INVALID_ARGUMENTS.exception();
        }
    }
}
