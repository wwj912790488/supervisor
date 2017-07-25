package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.settings.OutputPathValidateResult;
import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author hxiang
 */
public interface StorageMountRepository {
	
	public Map<String, String>getMounted() throws ShellException;

	public boolean mount(Storage s) throws ShellException;

	public boolean unmount(Storage s, boolean bRemoveFolder) throws ShellException;
	
	/**
	 * Checks the path is mounted or not.
	 * 
	 * @param path the local mounted path
	 * @return {@code true} if the path is mounted otherwise returns {@code false}
	 * @throws ShellException if failed to execute the mount -t command 
	 */
	public boolean isMounted(String path) throws ShellException;
	
	/**
	 * Checks the paths is mounted or not.
	 * 
	 * @param paths the local mounted paths
	 * @return {@code OutputPathValidateResult}
	 * @throws ShellException if failed to execute the mount -t command
	 */
	public OutputPathValidateResult isMounted(List<String> paths) throws ShellException;
	
	/**
	 * Retrieves the base folder of mounted.
	 * 
	 * @return the path of base mounted folder.
	 */
	public String getMountBaseDir();
}
