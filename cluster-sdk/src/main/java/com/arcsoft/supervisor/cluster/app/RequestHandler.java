package com.arcsoft.supervisor.cluster.app;

/**
 * This interface represents a request handler to execute the certain request.
 * 
 * @author fjli
 */
public interface RequestHandler {

	/**
	 * Execute the request.
	 * 
	 * @param request - the request to be processed
	 * @return response of the result
	 * @throws ActionException
	 */
	Response execute(Request request) throws ActionException;

}
