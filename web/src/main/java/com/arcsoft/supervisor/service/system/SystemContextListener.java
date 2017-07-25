package com.arcsoft.supervisor.service.system;

/**
 * System context listener. This listener will be notified when system context
 * is initialized or destroyed.
 * 
 * @author fjli
 */
public interface SystemContextListener {

	/**
	 * Notify when system context initialized.
	 */
	void contextInit();

	/**
	 * Notify when system context destroyed.
	 */
	void contextDestory();

}
