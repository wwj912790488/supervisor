package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.NTPStatus;
import com.arcsoft.supervisor.repository.settings.TimeRepository;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Implementation in centos for TimeDao
 * 
 * @author xpeng
 *
 */
@Repository
public class TimeRepositoryImplRHEL implements TimeRepository {
	private static final String ZONE_DIR = "/usr/share/zoneinfo/";	
	private static final String ZONE_SELF_CONFIG = "/etc/tz0001.conf";

	@Override
	public List<String> listTimeZone(String main) throws ShellException,
			IOException {
		File dir = new File(ZONE_DIR + main);
		String names[] = dir.list();
		Arrays.sort(names);		
		for(String name : names){			
			System.out.println(name);
		}
		return Arrays.asList(names);
	}

	@Override
	public void setTimezone(String timezone) throws ShellException, IOException {
		String cmd = "ln -sf " + ZONE_DIR + timezone + " /etc/localtime";
		StringWriter sw = new StringWriter();
		App.syncExec(null, cmd, sw);					
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(ZONE_SELF_CONFIG))){
			bw.write(timezone);
		}		
	}
	
	@Override
	public String getTimezone() throws ShellException, IOException {
		//get self config file
		try(BufferedReader br = new BufferedReader(new FileReader(ZONE_SELF_CONFIG))){
			return br.readLine();			
		}catch(FileNotFoundException e){
			String cmd = "cat /etc/sysconfig/clock";
			StringWriter sw = new StringWriter();
			App.syncExec(null, cmd, sw);
			String ret = sw.toString();
			String[] a = ret.split("\"");
			if(a.length > 2)
				return a[1];
		}
		return null;
	}

	@Override
	public void setSystemTime(Date date) throws ShellException, IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String strDate = df.format(date);

		String cmd = "date -s \"" + strDate + "\"";
		StringWriter sw = new StringWriter();
		App.syncExec(null, cmd, sw);
		//write the time to CMOS
		App.syncExec(null, "clock -w", sw);
	}

	@Override
	public NTPStatus getNTPStatus() throws ShellException, IOException {
		NTPStatus ntpStatus = new NTPStatus();
		//get service status;
		String cmd = "service ntpd status";
		StringWriter sw = new StringWriter();
		App.syncExec(null, cmd, sw);	
		String ret = sw.toString();
		if(ret.contains("running")){
			ntpStatus.setIsServiceOn(true);
		}
		
		//get ntp servers
		ntpStatus.setNtpServers(getNtpServers());
		
		return ntpStatus;
	}

	@Override
	public void syncWithNTP(NTPStatus ntp)
			throws ShellException, IOException {
		String cmd = null;
		StringWriter sw = null;
		
		// to start ntp service
		if (ntp.getIsServiceOn()) {
			List<String> servers = ntp.getNtpServers();
			if (servers.size() == 0)
				return;

			// first: stop ntpd service
			cmd = "service ntpd stop";
			sw = new StringWriter();
			App.syncExec(null, cmd, sw);

			// force to sync right now
			cmd = "ntpdate " + servers.get(0);
			sw = new StringWriter();
			App.syncExec(null, cmd, sw);

			// add server address to conf
			setNTPServers(servers);

			// start the ntp service
			cmd = "service ntpd start";
			sw = new StringWriter();
			App.syncExec(null, cmd, sw);

			// start service on startup
			cmd = "chkconfig ntpd on";
			sw = new StringWriter();
			App.syncExec(null, cmd, sw);
		} else {
			//stop ntp service
			cmd = "service ntpd stop";
			sw = new StringWriter();
			App.syncExec(null, cmd, sw);
		}
	}

	private List<String> getNtpServers() throws IOException {
		String filename = "/etc/ntp.conf";
		List<String> srevers = new ArrayList<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String strContent = null;
			while ((strContent = br.readLine()) != null) {
				if (strContent.matches("^server\\s.*$")) {
					String[] results = strContent.split("\\s+");
					if (results.length > 1 && results[1].length() > 0)
						srevers.add(results[1]);
				}
			}
		}

		return srevers;
	}

	private void setNTPServers(List<String> servers) throws IOException {
		String filename = "/etc/ntp.conf";
		String filetmp = "/etc/ntp.conf.tmp";
		StringBuffer sb = new StringBuffer();
		
		//read and replace server
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String strContent = null;
			while ((strContent = br.readLine()) != null) {
				if (strContent.startsWith("server")) {
					strContent = "";
				}
				sb.append(strContent).append("\n");
			}
		}	
		for(String server : servers){
			sb.append("server " + server + "\n");
		}
		
		//save the tmp file
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(filetmp))){
			bw.write(sb.toString());
		}
		
		File file = new File(filename);
		File tmp = new File(filetmp);
		file.delete();
		tmp.renameTo(file);
		
	}

}
