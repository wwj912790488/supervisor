package com.arcsoft.supervisor.model.domain.settings;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Bing
 * @author xpeng
 * 
 */
public class Eth {

	private String id;
	private String name = null;
	private Boolean isDHCP = true;
	private String ip = null;
	private String mask = null;
	private String gateway = null;
	private String hwaddr = null;
	private String nmctrl = "no";
	private String type = "Ethernet";
	private String uuid = null;
	private String onboot = "yes";
	private String dns1 = null;
	private String dns2 = null;
	private String status = null;
	private String speed = null;
	private Boolean isbond = false;
	private String master = null;
	private String primary = null;
	private Integer primaryReselect;
	private String activity = null;

    private int rate;

	/**
	 * The bonding mode, 0-6
	 */
	private int mode;
	/**
	 * The eth's function, include "input", "output".
	 */
	private String function = null;

	public Eth() {
	}

	public Eth(String id) {
		this.id = id;
		this.isDHCP = true;
	}

	public Eth(String id, String ip, String mask) {
		this.id = id;
		this.ip = ip;
		this.mask = mask;
		this.isDHCP = false;
	}

	public static Eth copy(Eth src) {
		Eth dest = new Eth(src.getId(), src.getIp(), src.getMask());
		dest.setIsDHCP(src.getIsDHCP());
		dest.setDns1(src.getDns1());
		dest.setDns2(src.getDns2());
		dest.setGateway(src.getGateway());
		dest.setHwaddr(src.getHwaddr());
		dest.setName(src.getName());
		dest.setNmctrl(src.getNmctrl());
		dest.setOnboot(src.getOnboot());
		dest.setStatus(src.getStatus());
		dest.setType(src.getType());
		dest.setUuid(src.getUuid());
		dest.setSpeed(src.getSpeed());
		dest.setActivity(src.getActivity());
		dest.setPrimary(src.getPrimary());
		dest.setPrimaryReselect(src.getPrimaryReselect());
		return dest;
	}

	public static boolean cmpAdvRoutes(List<Route> routes0, List<Route> routes) {
		boolean changed = false;
		if (routes0 == null && routes != null) {
			changed = true;
		} else if (routes0 != null && routes == null) {
			changed = true;
		} else if (routes0.size() != routes.size()) {
			changed = true;
		} else {
			for (Route route : routes) {
				if (!routes0.contains(route)) {
					changed = true;
					break;
				}
			}
		}
		return changed;
	}

	public boolean cmpNeedRestartSvc(Eth e) {
		if (e == null)
			return true;
		if (this.gateway == null && (e.gateway != null && e.gateway.length() > 0))
			return true;
		if (this.dns1 == null && (e.dns1 != null && e.dns1.length() > 0))
			return true;
		if (!this.gateway.equals(e.gateway) || this.dns1.equals(e.dns1))
			return true;
		if (this.isDHCP != e.isDHCP)
			return true;
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsDHCP() {
		return isDHCP;
	}

	public void setIsDHCP(Boolean isDHCP) {
		this.isDHCP = isDHCP;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getHwaddr() {
		return hwaddr;
	}

	public void setHwaddr(String hwaddr) {
		this.hwaddr = hwaddr;
	}

	public String getNmctrl() {
		return nmctrl;
	}

	public void setNmctrl(String nmctrl) {
		this.nmctrl = nmctrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOnboot() {
		return onboot;
	}

	public void setOnboot(String onboot) {
		this.onboot = onboot;
	}

	public String getDns1() {
		return dns1;
	}

	public void setDns1(String dns1) {
		this.dns1 = dns1;
	}

	public String getDns2() {
		return dns2;
	}

	public void setDns2(String dns2) {
		this.dns2 = dns2;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public Boolean getIsbond() {
		return isbond;
	}

	public void setIsbond(Boolean isbond) {
		this.isbond = isbond;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
	
	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}
	
	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}


	/**
	 * @return the primary
	 */
	public String getPrimary() {
		return primary;
	}

	
	/**
	 * @param primary the primary to set
	 */
	public void setPrimary(String primary) {
		this.primary = primary;
	}

	/**
	 * Return primary reselect option.
	 */
	public Integer getPrimaryReselect() {
		return primaryReselect;
	}

	/**
	 * Set the primary reselect option.
	 * 
	 * @param primaryReselect - the primary reselect option to set
	 */
	public void setPrimaryReselect(Integer primaryReselect) {
		this.primaryReselect = primaryReselect;
	}

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public boolean isRunning(){
        return StringUtils.isNotBlank(status) && status.equals("RUNNING");
    }
}
