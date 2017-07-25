package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.OutputPathValidateResult;
import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.repository.settings.StorageMountRepository;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * A implementation for {@link com.arcsoft.supervisor.repository.settings.StorageMountRepository}
 * to support mount operations under <tt>redhat</tt> likes linux system.
 *
 * @author Hxiang
 * @author zw
 *
 */
public class StorageMountRepositoryImplRHEL implements StorageMountRepository {

	/**
	 * Default base path of mount.
	 */
	private String mountBaseDir = "/mnt/data/remote";
	
	private static List<String> parseCmd(String cmd){
		List<String> ret = new ArrayList<String>();
		int p = 0;
		for(int i=0; i<cmd.length(); i++){
			char c = cmd.charAt(i);
			if(c==' '){
				continue;
			}else if(c=='"'){
				++i;
				p = cmd.indexOf('"', i);
			}else{
				p = cmd.indexOf(' ', i);
			}
			if(p==-1){
				p=cmd.length();
			}
			ret.add(cmd.substring(i, p));
			i=p;
		}
		return ret;
	}
	/**
	 * special exec for mount
	 * @param workdir
	 * @param cmd
	 * @param out
	 * @return
	 */
	static int mountExec(File workdir, String cmd, StringWriter out){	
		if(cmd==null || cmd.length()==0)
			return -1;
		
		int errCode = 0;
		try {	

			List<String> cmds = parseCmd(cmd);
			ProcessBuilder pb = new ProcessBuilder(cmds);
			if(workdir!=null)
				pb.directory(workdir);
			pb.redirectErrorStream(true);
			final Process proc = pb.start();
			
			Runnable exitChecker = new Runnable(){
				public void run(){		
					try {				
						proc.exitValue();						
					} catch (Exception e) {		
						try {
							proc.destroy();
						} catch (Throwable e1) {
							e1.printStackTrace();
						}	
					}			
				}			
			};
			
			Executors.newScheduledThreadPool(2).schedule(exitChecker, 180, TimeUnit.SECONDS);
				
			InputStreamReader inr = new InputStreamReader(proc.getInputStream());
			char[] buf = new char[512];
			int len;
			while( (len=inr.read(buf)) != -1){
				for(int i=0;i<len;i++){
					out.write(buf[i]);
				}
			}
			
		} catch (Exception e) {					
			System.out.println("Fail to exec: " + cmd + ", errmsg:\n" + e.getMessage());
		}		
		return errCode;
	}

	/**
	 * line pattern: //xye-xp-cn/test blank on /mnt/share type cifs (rw)
	 * @param line
	 * @return {src, dst}
	 */
	private static String[] mount_decodeDst(String line){			
		String son=" on ";
		int pOn = line.lastIndexOf(son);
		if(pOn==-1)
			return null;
		//System.out.println(line + "---- on pos:" + pOn);
		String dst = line.substring(0, pOn);
		String src = line.substring(pOn+son.length()).trim();
		src = src.substring(0, src.indexOf(' '));		
		return new String[]{src,dst};
	}

	@Override
	public Map<String, String> getMounted(){
		String p = getMountBaseDir();
		Map<String, String> ret = new HashMap<>();
		try {
			StringWriter sw = new StringWriter(512);
			App.syncExec(null, "mount -l", sw);
			String[] lines = sw.getBuffer().toString().split("[\\r\\n]+");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i].trim();
				if(line.indexOf("cifs")!=-1 || line.indexOf("nfs")!=-1){
					String[] dd = mount_decodeDst(line);
					if(dd!=null && dd[0].startsWith(p)){
						ret.put(getStorageName(dd[0]), dd[1]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public boolean mount(Storage s) throws ShellException{
		if(isMounted(s))
			return true;
				
		String remoteDst=s.getPath();
		String localdir= mountDir(s);
		
		File fldir = new File(localdir);
		if(!fldir.exists())
			fldir.mkdirs();
		
		StringBuilder cmd = new StringBuilder(256);
		if( ! "root".equals(System.getProperty("user.name")) ){
			cmd.append("sudo ");
		}
		
		cmd.append("mount ")
			.append(" -t ").append(s.getType())
			.append(' ').append(remoteDst.indexOf(' ')==-1? remoteDst : ('"' + remoteDst + '"'));
		cmd.append(' ').append(localdir);
		
		if(s.getUser()!=null && s.getUser().length()>0){
			cmd.append(" -o username=").append(s.getUser()).append(',')
				.append("password=").append(s.getPwd()==null?"":s.getPwd());
		}
	
		StringWriter sw = new StringWriter(512);
		mountExec(null, cmd.toString(), sw);
				
		if(isMounted(s))
			return true;
		else
			throw new ShellException(sw.toString());
	}

	@Override
	public boolean unmount(Storage s, boolean bRemoveFolder) throws ShellException{
		boolean isMounted = false;
		StringBuilder cmd = new StringBuilder(256);
		if( ! "root".equals(System.getProperty("user.name")) ){
			cmd.append("sudo ");
		}
		cmd.append("umount ").append(mountDir(s));
	
		StringWriter sw = new StringWriter(64);
		mountExec(null, cmd.toString(), sw);
		
		isMounted = isMounted(s);
		if(isMounted){ //try another way
			cmd.setLength(0);
			if( ! "root".equals(System.getProperty("user.name")) ){
				cmd.append("sudo ");
			}
			cmd.append("umount ").append('"').append(s.getPath()).append('"');
			mountExec(null, cmd.toString(), sw);
			isMounted = isMounted(s);
		}
		if (!isMounted)
		{
			if (bRemoveFolder){
				File file = new File(mountDir(s));
				file.delete();
			}
			return true;
		}
		else
			throw new ShellException(sw.toString());
	}
	
	@Override
	public boolean isMounted(String path) throws ShellException {
		OutputPathValidateResult result = isMounted(Arrays.asList(path));
		return result.isSuccess();
	}
	
	@Override
	public OutputPathValidateResult isMounted(List<String> paths) throws ShellException {
		OutputPathValidateResult result = new OutputPathValidateResult();
		Map<String, String> mountedStorages = getMounted();
		if(paths == null || paths.isEmpty() || mountedStorages.isEmpty()){
			return result;
		}
		List<String> storagePaths = new ArrayList<>();
		for(Entry<String, String> e : mountedStorages.entrySet()){
			storagePaths.add(getPathWithAppendSuffix(mountBaseDir + "/" + e.getKey()));
		}
		
		for(int i = 0; i < paths.size(); i++){
			paths.set(i, getPathWithAppendSuffix(paths.get(i)));
		}
		
		List<String> nonMountedPaths = new ArrayList<>();
		for(String path : paths){
			boolean isExists = false;
			for(String mountedPath : storagePaths){
				if(path.equalsIgnoreCase(mountedPath)){
					isExists = true;
					break;
				}
			}
			if(!isExists){
				nonMountedPaths.add(path);
			}
		}
		result.setNonMountedPaths(nonMountedPaths);
		if(nonMountedPaths.isEmpty()){
			result.setSuccess(true);
		}
		return result;
	}
	
	private String getPathWithAppendSuffix(String path){
		return path.endsWith("/") ? path : (path + "/");
	}
	
	private String getStorageName(String mountdir) {
		int p = mountdir.lastIndexOf('/');
		return p == -1 ? mountdir : mountdir.substring(p + 1);
	}
	
	private String mountDir(Storage s) {
		String p = getMountBaseDir();
		if (p != null && p.length() > 0 && !p.endsWith("/"))
			p += "/";
		return p + s.getName();
	}

	private boolean isMounted(Storage s) {
		return s.getPath().equals(getMounted().get(s.getName()));
	}
	
	public String getMountBaseDir() {
		return mountBaseDir;
	}
	public void setMountBaseDir(String mountBaseDir) {
		this.mountBaseDir = mountBaseDir;
	}
	
}
