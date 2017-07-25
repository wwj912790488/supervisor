package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.domain.settings.FirewallRule;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Request for deletting the firewall rule.
 * 
 * @author hxiang
 */
@XmlRootElement
public class DeleteFirewallRequest extends BaseRequest {

	private List<FirewallRule> rules = null;

	public DeleteFirewallRequest() {

	}

	public DeleteFirewallRequest(List<FirewallRule> rules) {
		this.setRules(rules);
	}

	public List<FirewallRule> getRules() {
		return rules;
	}

	public void setRules(List<FirewallRule> rules) {
		this.rules = rules;
	}
}
