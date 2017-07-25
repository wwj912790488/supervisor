package com.arcsoft.supervisor.agent.service.settings;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.settings.host.RebootRequest;
import com.arcsoft.supervisor.cluster.action.settings.host.RebootResponse;
import com.arcsoft.supervisor.cluster.action.settings.host.ShutdownRequest;
import com.arcsoft.supervisor.cluster.action.settings.host.ShutdownResponse;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.service.settings.LocalHostService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This service processes all host relation requests.
 * 
 * @author hxiang
 * @author fjli
 */
public class HostService implements ActionHandler {

    @Autowired
	private LocalHostService localHostService;

	public void setLocalHostService(LocalHostService localHostService) {
		this.localHostService = localHostService;
	}

	/**
	 * Returns all host requests.
	 */
	@Override
	public int[] getActions() {
		return new int[] {
				Actions.SYSTEM_REBOOT,
				Actions.SYSTEM_SHUTDOWN
		};
	}

	/**
	 * Receive host requests, and dispatch request to process methods.
	 * 
	 * @param request - the task request
	 * @return returns the response
	 * @throws ActionException if process request failed.
	 */
	@Override
	public Response execute(Request request) throws ActionException {
		if (request instanceof RebootRequest) {
			return reboot();
		} else if (request instanceof ShutdownRequest) {
			return shutdown();
		}
		return null;
	}

	/**
	 * Reboot server.
	 * 
	 * @return returns response indicate this action is success or not.
	 */
	private RebootResponse reboot() {
		RebootResponse resp = new RebootResponse();
		try {
			localHostService.reboot();
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (Exception e) {
			resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
		}
		return resp;
	}

	/**
	 * Reboot server.
	 * 
	 * @return returns response indicate this action is success or not.
	 */
	private ShutdownResponse shutdown() {
		ShutdownResponse resp = new ShutdownResponse();
		try {
			localHostService.shutdown();
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (Exception e) {
			resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
		}
		return resp;
	}

}
