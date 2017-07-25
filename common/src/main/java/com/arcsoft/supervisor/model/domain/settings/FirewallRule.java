package com.arcsoft.supervisor.model.domain.settings;

/**
 * 
 * firewall rule for iptables
 * 
 * @author hxiang
 * 
 */
public class FirewallRule {

	private String protocol;//tcp, ftp
	private String dport;// 80 or 1:800 range (destination port)
	private String target = "ACCEPT";// ACCEPT,DROP,REJECT

	public FirewallRule(){
		
	}
	
	public FirewallRule(String protocal, String dport){
		this.protocol = protocal;
		this.dport = dport;
	}
	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getDport() {
		return dport;
	}

	public void setDport(String dport) {
		this.dport = dport;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	@Override
	public String toString(){
		return protocol + ": " + dport;
	}
}
