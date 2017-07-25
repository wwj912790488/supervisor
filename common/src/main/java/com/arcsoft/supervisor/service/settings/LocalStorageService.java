package com.arcsoft.supervisor.service.settings;

import com.arcsoft.supervisor.model.domain.settings.OutputPathValidateResult;
import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.util.List;
import java.util.Map;

/**
 * This service is used to set storage of the agent.
 * 
 * @author hxiang
 * @author zw
 */
public interface LocalStorageService {

	public Map<String, String> getRemoteMounted() throws ShellException;
	public void mountStorage(Storage s) throws Exception;
	public void umountStorage(Storage s) throws Exception;

	public void addRemoteStorage(Storage st) ;
	public void delRemoteStorage(Integer id) throws ShellException;
	public List<Storage> findAllRemoteStorages();

	public Storage getRemoteStorage(Integer id);
	public Storage getRemoteStorageByName(String name);

	public void updateStorage(Storage s) throws Exception;
	
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
