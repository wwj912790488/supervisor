package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.settings.Route;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.util.List;

/**
 * @author hxiang
 * 
 */
public interface RouteRepository {

	public abstract List<Route> getRoutes() throws ShellException;

	public abstract void add(Route route) throws ShellException;

	public abstract void delete(Route route) throws ShellException;

}