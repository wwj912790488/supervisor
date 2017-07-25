package com.arcsoft.supervisor.cluster.action;

import com.arcsoft.supervisor.cluster.app.RequestHandler;

/**
 * Process the specified actions.
 * 
 * @author fjli
 */
public interface ActionHandler extends RequestHandler {

	/**
	 * Returns all supported actions.
	 */
	int[] getActions();

}
