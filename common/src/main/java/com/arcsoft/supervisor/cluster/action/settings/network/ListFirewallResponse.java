package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.settings.FirewallRule;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Response for getting firewall rules
 * 
 * @author hxiang
 */
@XmlRootElement
public class ListFirewallResponse extends BaseResponse {

	private List<FirewallRule> rules;

	public List<FirewallRule> getRules() {
		return rules;
	}

	public void setRules(List<FirewallRule> rules) {
		this.rules = rules;
	}
}
