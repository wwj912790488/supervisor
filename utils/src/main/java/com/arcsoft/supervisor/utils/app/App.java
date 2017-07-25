package com.arcsoft.supervisor.utils.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * class App
 * 
 * @author Bing
 * 
 */
public class App {

	private static Logger log = LoggerFactory.getLogger(App.class);

	/**
	 * run shell
	 * 
	 * @param strShell
	 *            the string which will be run;
	 * @throws java.io.IOException
	 * @throws InterruptedException
	 */
	public static List<String> runShell(String strShell) throws ShellException {
		List<String> strList = new ArrayList<String>();
		Process process;
		boolean isSuccess = false;
		try {
			process = Runtime.getRuntime().exec(
					new String[] { "/bin/sh", "-c", strShell}, null, null);

			log.debug("runShell: " + strShell);
			process.waitFor();
			isSuccess = process.exitValue() == 0;
			
			if (isSuccess){
				InputStreamReader ir = new InputStreamReader(
						process.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);
				
				String line;
				while ((line = input.readLine()) != null) {
					strList.add(line);
				}
				log.debug("result: " + strList);
			}else{
				// get the process's errorStream
				char[] buf = new char[512];
				StringWriter out = new StringWriter();
				InputStream errorStream = process.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						errorStream));
				int len;
				while ((len = reader.read(buf)) != -1) {
					for (int i = 0; i < len; i++) {
						out.write(buf[i]);
					}
				}
				throw new Exception(out.toString());
			}			
		} catch (Exception e) {
			log.error("runShell: " + strShell + " failed: " + e.getMessage());
			//TODO:throw shell exception have no message, the view will show custom message.
			throw new ShellException("", e.getCause());
		}
		return strList;
	}

	public static int syncExec(File workdir, String cmd, Writer out) throws ShellException {
		log.debug("syncExec: " + cmd);

		int errCode = -1;
		try {
			if (cmd != null && cmd.length() > 0) {
				List<String> cmds = parseCmd(cmd);
				ProcessBuilder pb = new ProcessBuilder(cmds);
				if (workdir != null)
					pb.directory(workdir);
				Process proc = pb.start();
				
				InputStreamReader inr = new InputStreamReader(
						proc.getInputStream());
				char[] buf = new char[512];
				int len;
				while ((len = inr.read(buf)) != -1) {
					for (int i = 0; i < len; i++) {
						out.write(buf[i]);
					}
				}
				errCode = proc.waitFor();
			}
		} catch (Exception e) {
			log.error("syncExec " + cmd + " failed: " + e.getMessage());
			throw new ShellException(e.getMessage(), e.getCause());
		}

		log.debug("result: " + out.toString());
		return errCode;
	}

	private static List<String> parseCmd(String cmd) {
		List<String> ret = new ArrayList<String>();
		int p = 0;
		for (int i = 0; i < cmd.length(); i++) {
			char c = cmd.charAt(i);
			if (c == ' ') {
				continue;
			} else if (c == '"') {
				++i;
				p = cmd.indexOf('"', i);
			} else {
				p = cmd.indexOf(' ', i);
			}
			if (p == -1) {
				p = cmd.length();
			}
			ret.add(cmd.substring(i, p));
			i = p;
		}
		return ret;
	}

}
