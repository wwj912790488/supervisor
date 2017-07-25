package com.arcsoft.supervisor.agent.service.settings;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.settings.network.*;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.service.settings.LocalFirewallService;
import com.arcsoft.supervisor.utils.app.ShellException;

/**
 * This service processes all firewall relation requests.
 * 
 * @author hxiang
 * @author fjli
 */
public class FirewallService implements ActionHandler {

	private LocalFirewallService localFirewallService;

	public void setLocalFirewallService(LocalFirewallService localFirewallService) {
		this.localFirewallService = localFirewallService;
	}

	/**
	 * Returns all firewall requests.
	 */
	@Override
	public int[] getActions() {
		return new int[] {
				Actions.FIREWALL_LIST,
				Actions.FIREWALL_ADD,
				Actions.FIREWALL_DELETE,
				Actions.FIREWALL_START,
				Actions.FIREWALL_STOP,
				Actions.FIREWALL_GET_STATUS
		};
	}

	/**
	 * Receive firewall requests, and dispatch request to process methods.
	 * 
	 * @param request - the task request
	 * @return returns the response
	 * @throws ActionException if process request failed.
	 */
	@Override
	public Response execute(Request request) throws ActionException {
		if (request instanceof ListFirewallRequest) {
			return listFirewall();
		} else if (request instanceof AddFirewallRequest) {
			return addFirewall((AddFirewallRequest) request);
		} else if (request instanceof DeleteFirewallRequest) {
			return deleteFirewall((DeleteFirewallRequest) request);
		} else if (request instanceof StartFirewallRequest) {
			return startFirewall();
		} else if (request instanceof StopFirewallRequest) {
			return stopFirewall();
		} else if (request instanceof GetFirewallStatusRequest) {
			return getFirewallStatus();
		}
		return null;
	}

	/**
	 * Get firewall rules list.
	 * 
	 * @return returns response including the firewall rules list.
	 */
	private ListFirewallResponse listFirewall() {
		ListFirewallResponse resp = new ListFirewallResponse();
		try{
			resp.setRules(localFirewallService.getFirewalls());
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		}catch (ShellException e) {
			resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
		} catch (Exception e) {
			resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
		}
		return resp;
	}

	/**
	 * Add new firewall rule.
	 * 
	 * @param request - the add firewall request
	 * @return returns response indicating the action is success or not.
	 */
	private AddFirewallResponse addFirewall(AddFirewallRequest request) {
		AddFirewallResponse resp = new AddFirewallResponse();
		try {
			localFirewallService.addFirewall(request.getRules().get(0));
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (ShellException e) {
			resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
		} catch (Exception e) {
			resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
		}
		return resp;
	}

	/**
	 * Delete new firewall rule.
	 * 
	 * @param request - the delete firewall request
	 * @return returns response indicating the action is success or not.
	 */
	private DeleteFirewallResponse deleteFirewall(DeleteFirewallRequest request) {
		DeleteFirewallResponse resp = new DeleteFirewallResponse();
		try {
			localFirewallService.deleteFirewall(request.getRules().get(0));
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (ShellException e) {
			resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
		} catch (Exception e) {
			resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
		}
		return resp;
	}

	/**
	 * Start firewall service.
	 * 
	 * @return returns response indicating the action is success or not.
	 */
	private StartFirewallResponse startFirewall() {
		StartFirewallResponse response = new StartFirewallResponse();
		try {
			localFirewallService.startFirewall();
			response.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (ShellException e) {
			response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
		}
		return response;
	}

	/**
	 * Stop firewall service.
	 * 
	 * @return returns response indicating the action is success or not.
	 */
	private StopFirewallResponse stopFirewall() {
		StopFirewallResponse response = new StopFirewallResponse();
		try {
			localFirewallService.stopFirewall();
			response.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (ShellException e) {
			response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
		}
		return response;
	}

	/**
	 * Get firewall service is on or off.
	 * 
	 * @return returns response including the firewall service status.
	 */
	private GetFirewallStatusResponse getFirewallStatus() {
		GetFirewallStatusResponse response = new GetFirewallStatusResponse();
		try {
			response.setServiceOn(localFirewallService.isFirewallOn());
			response.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (Exception e) {
			response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
		}
		return response;
	}

}
