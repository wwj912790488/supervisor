package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.Eth;
import com.arcsoft.supervisor.repository.settings.EthRepository;
import com.arcsoft.supervisor.utils.StringHelper;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EthDao Impl for centos (RHEL) Remove DNS, route from this class on 2013-07-24 by xpeng
 * 
 * @author Bing
 * @author xpeng
 * @author zw
 * 
 */
@Repository
public class EthRepositoryImplRHEL implements EthRepository {

	private static Logger log = LoggerFactory.getLogger(EthRepositoryImplRHEL.class);

	private static final String PREFIX_CFGETH = "ifcfg-";

	private static File getNetworkscriptDir() {
		return new File("/etc/sysconfig/network-scripts");
	}

	private static String decodeProp(String val) {
		return val == null ? null : val.replace('"', ' ').trim();
	}

	private static boolean isStaticIp(Properties prop) {
		String bootproto = prop.getProperty("BOOTPROTO");
		return !("dhcp".equalsIgnoreCase(decodeProp(bootproto)));
	}

	private static Properties loadEth(File ethi) throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(ethi);
		try {
			prop.load(fis);
			return prop;
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
			}
		}
	}

	private static void storeEth(Properties prop, File ethi) throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ethi), "utf-8"));
		try {
			for (String name : prop.stringPropertyNames())
				writer.println(name + "=" + prop.getProperty(name));
		} finally {
			writer.close();
		}
	}

	private static Eth propToEth(Properties prop) {
		Eth eth = new Eth(decodeProp(prop.getProperty("DEVICE")));
		boolean staticIP = isStaticIp(prop);
		eth.setIsDHCP(!staticIP);
		eth.setName(decodeProp(prop.getProperty("NAME")));
		if (staticIP) {
			eth.setIp(decodeProp(prop.getProperty("IPADDR")));
			eth.setMask(decodeProp(prop.getProperty("NETMASK")));
			eth.setGateway(decodeProp(prop.getProperty("GATEWAY")));
			eth.setDns1(decodeProp(prop.getProperty("DNS1")));
			eth.setDns2(decodeProp(prop.getProperty("DNS2")));
		}
		if ("yes".equalsIgnoreCase(decodeProp(prop.getProperty("SLAVE")))
				|| "yes".equalsIgnoreCase(decodeProp(prop.getProperty("slave")))) {
			eth.setMaster(decodeProp(prop.getProperty("MASTER")));
		}
		setBondOptions(eth,decodeProp(prop.getProperty("BONDING_OPTS")));
		return eth;
	}

	private static void fillEth(Eth eth) throws ShellException {
		StringWriter sw = new StringWriter(512);
		App.syncExec(null, "ifconfig " + eth.getId(), sw);
		String ret = sw.toString();

		String[] lines = ret.split("[\\r\\n]+");
		for (int j = 0; j < lines.length; j++) {
			String line = lines[j].trim();
			if (line.startsWith("inet addr:")) {
				if (eth.getIsDHCP()) {
					String[] arr = line.split("\\s");
					for (int i = 0; i < arr.length; i++) {
						if (arr[i].startsWith("addr:")) {
							eth.setIp(arr[i].substring(arr[i].indexOf(':') + 1).trim());
						} else if (arr[i].startsWith("Mask:")) {
							eth.setMask(arr[i].substring(arr[i].indexOf(':') + 1).trim());
						}
					}
				}
			} else if (line.indexOf("RUNNING") != -1) {
				eth.setStatus("RUNNING");
			}
			eth.setActivity(getActivityEthID(eth.getId()));
		}
	}

//	// test code for when running on windows
//	static List<Eth> eths = new ArrayList<Eth>();
//	static {
//		Eth eth = new Eth("eth0", "172.17.167.71", "255.255.255.251");
//		eth.setGateway("172.17.155.21");
//		eths.add(eth);
//		Eth eth1 = new Eth("eth1", "172.17.167.72", "255.255.255.252");
//		eth1.setGateway("172.17.155.22");
//		eths.add(eth1);
//		Eth eth2 = new Eth("eth2", "172.17.167.73", "255.255.255.253");
//		eth2.setGateway("172.17.155.23");
//		eth2.setIsDHCP(true);
//		eth2.setMaster("bond0");
//		eths.add(eth2);
//		Eth eth3 = new Eth("eth3", "172.17.167.74", "255.255.255.254");
//		eth3.setGateway("172.17.155.24");
//		eth3.setIsDHCP(false);
//		eth3.setMaster("bond0");
//		eths.add(eth3);
//		Eth eth4 = new Eth("eth4", "172.17.167.75", "255.255.255.251");
//		eth4.setGateway("172.17.155.21");
//		eths.add(eth4);
//		Eth eth5 = new Eth("eth5", "172.17.167.76", "255.255.255.252");
//		eth5.setGateway("172.17.155.22");
//		eths.add(eth5);
//		Eth eth6 = new Eth("eth6", "172.17.167.77", "255.255.255.253");
//		eth6.setGateway("172.17.155.23");
//		eth6.setIsDHCP(true);
//		eth6.setFunction("input,output");
//		eths.add(eth6);
//		Eth eth7 = new Eth("eth7", "172.17.167.78", "255.255.255.254");
//		eth7.setGateway("172.17.155.24");
//		eth7.setIsDHCP(false);
//		eth7.setFunction("output");
//		eths.add(eth7);
//		Eth bond0 = new Eth("bond0", "172.17.167.78", "255.255.255.254");
//		bond0.setGateway("172.17.155.24");
//		bond0.setIsDHCP(false);
//		eths.add(bond0);
//	}

	@Override
	public List<Eth> findAllEths() throws IOException, ShellException {
		// return eths;
		ArrayList<Eth> ret = new ArrayList<Eth>();
		// find all bond eths
		ret.addAll(findAllEths(true));
		// find all free eths
		ret.addAll(findAllEths(false));

		return ret;
	}

	private List<Eth> findAllEths(boolean isbond) throws IOException, ShellException {
		try {
			final File dir = getNetworkscriptDir();
			final boolean isbondfinal = isbond;
			String[] names = dir.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
//					String prefix = null;
//					if (isbondfinal) {
//						prefix = PREFIX_CFGETH + "bond";
//					} else {
//						prefix = PREFIX_CFGETH + "eth";
//					}
//					return name.startsWith(prefix);				
					
					String regex = null;
					if(isbondfinal){						
						regex = "ifcfg-bond\\d+$";
					}else{
						regex = "ifcfg-eth\\d+$";
					}
					if (name.matches(regex)) {
						return true;
					}
					return false;					
				}
			});
			ArrayList<Eth> ret = new ArrayList<Eth>();
			if (names != null) {
				Arrays.sort(names);
				for (int i = 0; i < names.length; i++) {
					File ethi = new File(dir, names[i]);
					Eth theEth = propToEth(loadEth(ethi));
					theEth.setIsbond(isbond);
					if (theEth.getId() == null) {
						theEth.setId(names[i].substring(names[i].lastIndexOf('-') + 1));
					}
					fillEth(theEth);
					if (!isbond) {
						int sp = getEthSpeed(theEth.getId());
                        theEth.setRate(getEthUsedRate(theEth.getId()));
						theEth.setSpeed(sp < 1024 ? sp + "Mbps" : sp / 1024 + "Gbps");
					}
					ret.add(theEth);
				}
			}
			return ret;
		} catch (ShellException se) {
			log.error("listEth shell error: " + se.getMessage() + se.getCause());
			throw se;
		} catch (IOException ie) {
			log.error("listEth io error: " + ie.getMessage() + ie.getCause());
			throw ie;
		} catch (Exception e) {
			log.error("listEth other error: " + e.getMessage() + e.getCause());
			throw new IOException(e.getMessage(), e.getCause());
		}
	}

	private static void restartEth(String ethId) throws ShellException {
		exec("ifdown " + ethId);
		exec("ifup " + ethId);
	}

	@Override
	public void updateEth(Eth eth) throws IOException {
		// eths.get(2).setIp(eth.getIp());
		// eths.get(2).setMask(eth.getMask());
		// eths.get(2).setGateway(eth.getGateway());
		// eths.get(2).setIsDHCP(eth.getIsDHCP());
		// eths.get(2).setFunction(eth.getFunction());
		// return;
		try {
			long start = System.currentTimeMillis();
			log.debug("updateEth start");
			boolean changed = false;
			File ethi = new File(getNetworkscriptDir(), PREFIX_CFGETH + eth.getId());
			// Properties prop = loadEth(ethi);
			Properties prop = null;
			if (ethi.exists()) {
				prop = loadEth(ethi);
			} else {
				prop = new Properties();
				prop.setProperty("DEVICE", eth.getId());
			}
			log.debug("updateEth,id = " + eth.getId() + ",primary = "  +eth.getPrimary() + ",isBond = " + eth.getIsbond());
			// check ip static
			if (isStaticIp(prop) == eth.getIsDHCP()) {
				prop.setProperty("BOOTPROTO", eth.getIsDHCP() ? "dhcp" : "static");
				changed = true;
			}

			String[][] editable = { { "IPADDR", eth.getIp() }, { "NETMASK", eth.getMask() },
					{ "GATEWAY", eth.getGateway() }, { "TYPE", "Ethernet" }, { "ONBOOT", "yes" }, {"NM_CONTROLLED", "no"}};

			for (int i = 0; i < editable.length; i++) {
				String k = editable[i][0];
				String val1 = editable[i][1];
				String val0 = decodeProp(prop.getProperty(k));
				if (val1 == null || val1.length() == 0) {
					if (val0 != null) {
						prop.remove(k);
						changed = true;
					}
				} else if (!val1.equalsIgnoreCase(val0)) {
					prop.setProperty(k, val1);
					changed = true;
				}
			}

			Map<String, String> bondingOptionsMap = new LinkedHashMap<>();
			if (eth.getIsbond()) {
				bondingOptionsMap.put("mode", String.valueOf(eth.getMode()));
				bondingOptionsMap.put("miimon", "100");
				bondingOptionsMap.put("primary", eth.getPrimary());
				bondingOptionsMap.put("primary_reselect", String.valueOf(eth.getPrimaryReselect()));
			}

			// check the bonding options is changed or not.
			if (!changed && eth.getIsbond()) {
				String bondingOptions = decodeProp(prop.getProperty("BONDING_OPTS"));
				Map<String, String> options = parseSubOptions(bondingOptions);
				if (!compareSubOptionsMap(bondingOptionsMap, options))
					changed = true;
			}

			if (changed) {
				if (eth.getIsbond())
					prop.setProperty("BONDING_OPTS", buildSubOptions(bondingOptionsMap));
				storeEth(prop, ethi);
				restartEth(eth.getId());
			}
			long spend = System.currentTimeMillis() - start;
			log.debug("updateEth end, spend: " + spend + "(ms)");
		} catch (IOException ie) {
			log.error("updateEth io error: " + ie.getMessage() + ie.getCause());
			throw ie;
		} catch (Exception e) {
			log.error("updateEth other error: " + e.getMessage() + e.getCause());
			throw new IOException(e.getMessage(), e.getCause());
		}
	}

	private static String buildSubOptions(Map<String, String> options) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\"");
		boolean hasOption = false;
		for (Entry<String, String> option : options.entrySet()) {
			if (option.getValue() == null)
				continue;
			if (hasOption)
				buffer.append(" ");
			else
				hasOption = true;
			buffer.append(option.getKey()).append("=").append(option.getValue());
		}
		buffer.append("\"");
		return buffer.toString();
	}

	private static Pattern p = Pattern.compile("^\\s*([^\\s]*)\\s*=\\s*([^\\s]*)\\s*$");

	private static Map<String, String> parseSubOptions(String options) {
		Map<String, String> map = new LinkedHashMap<>();
		if (options != null) {
			String[] optionsArray = options.split("\\s+");
			for (String option : optionsArray) {
				Matcher m = p.matcher(option);
				if (m.matches())
					map.put(m.group(1), m.group(2));
			}
		}
		return map;
	}

	private static boolean compareSubOptionsMap(Map<String, String> options1, Map<String, String> options2) {
		if (options1.size() != options2.size())
			return false;
		if (!options1.isEmpty()) {
			for (String option : options1.keySet()) {
				if (!options2.containsKey(option))
					return false;
				if (!optionEquals(options1.get(option), options2.get(option)))
					return false;
			}
		}
		return true;
	}

	private static boolean optionEquals(String option1, String option2) {
		return (option1 != null) ? (option1.equalsIgnoreCase(option2)) : (option2 == null);
	}

	private static class EthSample {

		long bytesTime = 0;
		long bytesRx = 0;
		long bytesTx = 0;
		int ret = 0;
	}

	/** eth speed cache - max 16 */
	private static int[] ethSpeed = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	/** eth samples */
	private static EthSample[] ethSamples = new EthSample[ethSpeed.length];

	private int getEthSpeed(String ethId) throws ShellException, IOException {
		try {
			int ethIndex = Integer.parseInt(ethId.substring(3));
			if (ethIndex < ethSpeed.length && ethSpeed[ethIndex] != 0) {
				return ethSpeed[ethIndex];
			}
			int ret = 0;
			String cmd = "sh -c \"ethtool eth" + ethIndex + " | grep Speed\"";
			StringWriter out = new StringWriter();
			App.syncExec(null, cmd, out);
			String res = out.getBuffer().toString();
			int p = res.indexOf(':');
			int p11 = res.indexOf("Mb");
			int p12 = res.indexOf("Gb");
			int p1 = p12 != -1 ? p12 : p11;
			if (p != -1 & p1 != -1) {
				ret = Integer.parseInt(res.substring(p + 1, p1).trim());
				if (p12 != -1)
					ret *= 1024;
			}

			if (ethIndex < ethSpeed.length) {
				ethSpeed[ethIndex] = ret;
			}
			return ret;
		} catch (ShellException e) {
			log.error("getSpeed error: " + e.getMessage() + " Caused by: " + e.getCause());
			throw e;
		} catch (Exception e) {
			log.error("getSpeed other error: " + e.getMessage() + " Caused by: " + e.getCause());
			throw new IOException(e.getMessage(), e.getCause());
		}
	}
	
//	private void resetSpeed(String ethId){		
//		int ethIndex = Integer.parseInt(ethId.substring(3));
//		if (ethIndex < ethSpeed.length && ethSpeed[ethIndex] != 0) {
//			ethSpeed[ethIndex] = 0;
//		}
//	}
	private boolean checkEth(String ethId) throws ShellException {
		StringWriter sw = new StringWriter(512);
		App.syncExec(null, "ifconfig " + ethId, sw);
		String ret = sw.toString();

		String[] lines = ret.split("[\\r\\n]+");
		for (int j = 0; j < lines.length; j++) {
			String line = lines[j].trim();
			if (line.indexOf("RUNNING") != -1) {
				return true;
			}
		}
		return false;
	}

	private static String getActivityEthID(String masterID){
		StringWriter sw = new StringWriter(512);
		String activity = null;
		try {
			App.syncExec(null, "cat /proc/net/bonding/" + masterID, sw);
			String ret = sw.toString();
			String[] lines = ret.split("[\\r\\n]+");
			for (int j = 0; j < lines.length; j++) {
				String line = lines[j].trim();
				String[] arr = line.split("\\s");
				if (line.indexOf("Currently Active Slave:") != -1) {
					activity = arr[3].trim();
					log.debug("getActivityEthID,activity = " + activity);
					break;
				}
			}
		} catch (ShellException e) {
			log.debug("getActivityEthID ShellException,e:" + e.getMessage());
		}
		return activity;
	}
	
	public int getEthUsedRate(String ethId) throws ShellException, IOException {
		try {
			int ethIndex = Integer.parseInt(ethId.substring(3));
			if (ethIndex < 0 || ethIndex >= ethSamples.length) {
				return 0;
			}
			//if eth is disconnected, return -1 directly
			if(!checkEth(ethId)){
				//resetSpeed(ethId);
				return -1;
			}
			
			int ethSpeed = getEthSpeed(ethId);
			if (ethSpeed == 0)
				return 0;

			if (ethSamples[ethIndex] == null) {
				ethSamples[ethIndex] = new EthSample();
			}

			long n = System.currentTimeMillis();
			long interval = n - ethSamples[ethIndex].bytesTime;
			if (interval > 2000) { // 2s min req interval
				EthSample s = new EthSample();
				s.bytesTime = n;
				s.bytesRx = getEthBytes(ethId, true);
				s.bytesTx = getEthBytes(ethId, false);
				if (interval > 300000) { // > 5 minites, then get again
					EthSample tmp = ethSamples[ethIndex];
					ethSamples[ethIndex] = s;
					s = tmp;
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					s.bytesTime = System.currentTimeMillis();
					s.bytesRx = getEthBytes(ethId, true);
					s.bytesTx = getEthBytes(ethId, false);

				}
				long bytes = (s.bytesRx + s.bytesTx - ethSamples[ethIndex].bytesRx - ethSamples[ethIndex].bytesTx); // bytes
				long rate = bytes * 1000 / (s.bytesTime - ethSamples[ethIndex].bytesTime); // Bps
				s.ret = (int) (rate * 8 * 10000 / 1024 / 1024 / ethSpeed);// val * 10000			
				if(s.ret < 0){
					//if change eth's setting, the new data will be zero, the ret will be minus.
					s.ret = 0;
				}
				if (log.isDebugEnabled()) {
					log.debug("============" + ethIndex + "=============");
					log.debug("interval: " + (s.bytesTime - ethSamples[ethIndex].bytesTime));
					log.debug("bytes = " + bytes + ", rate = " + rate + "Bps, percent = " + s.ret + "%");
				}
				ethSamples[ethIndex] = s;
			}

			return ethSamples[ethIndex].ret;
		} catch (ShellException e) {
			log.error("get used rate error: " + e.getMessage() + " Caused by: " + e.getCause());
			throw e;
		} catch (Exception e) {
			log.error("get used rate other error: " + e.getMessage() + " Caused by: " + e.getCause());
			throw new IOException(e.getMessage(), e.getCause());
		}
	}

	private static long getEthBytes(String ethId, boolean rx) throws IOException {
		FileInputStream fis = null;
		try {
			StringBuilder path = new StringBuilder(64);
			path.append("/sys/class/net/").append(ethId).append("/statistics/").append(rx ? "rx_bytes" : "tx_bytes");
			File file = new File(path.toString());
			String data = null;

			byte[] buf = new byte[256];
			fis = new FileInputStream(file);
			int len = fis.read(buf);
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					if (buf[i] == 0) {
						len = i;
						break;
					}
				}
				data = new String(buf, 0, len);
				data = data.trim();
			}
			log.debug(path.toString() + ":" + data);
			return Long.parseLong(data);
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e.getCause());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void bond(Eth mbond, String[] slaveEthId) throws IOException, ShellException {
		try {
			long start = System.currentTimeMillis();
			log.debug("bond start");
			if (slaveEthId != null && slaveEthId.length < 2)
				return;

			// find old slaves
			ArrayList<String> slaveList = new ArrayList<String>();
			List<Eth> allEths = findAllEths(false);
			for (Eth e : allEths) {
				if (mbond.getId().equals(e.getMaster())) {
					slaveList.add(e.getId());
				}
			}

			// ubound old slaves
			String[] oldSlaveEthId = slaveList.toArray(new String[0]);
			for (int i = 0; i < oldSlaveEthId.length; i++) {
				// ubond slaves
				exec("ifenslave -d " + mbond.getId() + " " + oldSlaveEthId[i]);
				// TODO: when ubound, dhcp or none;
				Properties pm = new Properties();
				pm.setProperty("DEVICE", oldSlaveEthId[i]);
				pm.setProperty("BOOTPROTO", "none");// if set as dhcp, will spend too long(5s) to determine ip.
				pm.setProperty("TYPE", "Ethernet");
				pm.setProperty("ONBOOT", "yes");
				storeEth(pm, new File(getNetworkscriptDir(), PREFIX_CFGETH + oldSlaveEthId[i]));

				restartEth(oldSlaveEthId[i]);
			}

			if (slaveEthId == null) {
				exec("ifdown " + mbond.getId());
				File bondf = new File(getNetworkscriptDir(), PREFIX_CFGETH + mbond.getId());
				bondf.delete();
				updateModProbe(mbond.getId(), true);
			} else {
				for (int i = 0; i < slaveEthId.length; i++) {
					Properties pm = new Properties();
					pm.setProperty("DEVICE", slaveEthId[i]);
					pm.setProperty("BOOTPROTO", "none");
					pm.setProperty("TYPE", "Ethernet");
					pm.setProperty("ONBOOT", "yes");
					pm.setProperty("NM_CONTROLLED", "no");
					pm.setProperty("MASTER", mbond.getId());
					pm.setProperty("SLAVE", "yes");
					// update slaves
					storeEth(pm, new File(getNetworkscriptDir(), PREFIX_CFGETH + slaveEthId[i]));
					restartEth(slaveEthId[i]);

				}
				// update master
				mbond.setIsbond(true);
				updateEth(mbond);
				updateModProbe(mbond.getId(), false);
			}
			long spend = System.currentTimeMillis() - start;
			log.debug("bond end, spend: " + spend + "(ms)");
		} catch (IOException ie) {
			log.error("bond io error: " + ie.getMessage() + ie.getCause());
			throw ie;
		} catch (Exception e) {
			log.error("bond other error: " + e.getMessage() + e.getCause());
			throw new IOException(e.getMessage(), e.getCause());
		}
	}

	private void updateModProbe(String bondId, boolean rm) throws IOException {
		File fmp = new File("/etc/modprobe.d/dist.conf");
		StringBuffer sb = new StringBuffer();
		boolean needModified = true;
		try (BufferedReader br = new BufferedReader(new FileReader(fmp))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("alias " + bondId)) {
					if (rm)
						continue;
					else {
						needModified = false;
					}

				} else
					sb.append(line).append("\n");
			}
			if (needModified && !rm)
				sb.append("alias " + bondId + " bonding");
		}

		if (needModified) {
			try (BufferedWriter fos = new BufferedWriter(new FileWriter(fmp))) {
				fos.write(sb.toString());
			}
		}
	}

	private static void setBondOptions(Eth eth,String strOption) {
		if (strOption != null && eth != null) {
			Map<String, String> options = parseSubOptions(strOption);
			eth.setMode(StringHelper.toInteger(options.get("mode"), 0));
			eth.setPrimary(options.get("primary"));
			eth.setPrimaryReselect(StringHelper.toInteger(options.get("primary_reselect")));
		}
	}

	private static String exec(String cmd) throws ShellException {
		String sudo;
		if (!"root".equals(System.getProperty("user.name"))) {
			sudo = "sudo ";
		} else {
			sudo = "";
		}
		StringWriter sw = new StringWriter();
		App.syncExec(null, sudo + cmd, sw);
		String res = sw.toString();
		return res.length() == 0 ? null : res;
	}

}
