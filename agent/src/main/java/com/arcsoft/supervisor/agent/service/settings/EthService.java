package com.arcsoft.supervisor.agent.service.settings;

import com.arcsoft.supervisor.agent.config.AppConfig;
import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.settings.network.*;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.model.domain.settings.Eth;
import com.arcsoft.supervisor.repository.settings.EthRepository;
import com.arcsoft.supervisor.repository.settings.impl.EthRepositoryImplRHEL;
import com.arcsoft.supervisor.service.settings.LocalEthService;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This service processes all Ethernet settings requests.
 * 
 * @author xpeng
 * @author fjli
 */
public class EthService implements ActionHandler {

	private static final String DEFAULT_ETH_FUNCTION_LOCATION = "../data/networks.properties";
	private static final String[] functions = new String[] { "input", "output" };
	private Logger log = Logger.getLogger(EthService.class);
	private EthSettingsListener listener;
	private File configFile;

	private LocalEthService localEthService;

	public EthService() {
		configFile = new File(AppConfig.getString("network.settings.file", DEFAULT_ETH_FUNCTION_LOCATION)).getAbsoluteFile();
	}

	public void setLocalEthService(LocalEthService localEthService) {
		this.localEthService = localEthService;
	}

	public void setEthSettingsListener(EthSettingsListener listener) {
		this.listener = listener;
	}

	public void init() {
		if (listener != null) {
			try {
				Properties prop = loadSettingsFromFile();
				if (!prop.isEmpty())
					listener.ethSettingsChanged(prop);
			} catch (IOException e) {
				log.error("load network settings failed.", e);
			}
		}
	}

	/**
	 * Returns all Ethernet settings requests.
	 */
	@Override
	public int[] getActions() {
		return new int[] { Actions.NETWORK_LIST, Actions.NETWORK_STAT, Actions.NETWORK_SAVE, Actions.NETWORK_BOND };
	}

	/**
	 * Receive Ethernet settings requests, and dispatch request to process methods.
	 * 
	 * @param request - the task request
	 * @return returns the response
	 * @throws ActionException if process request failed.
	 */
	@Override
	public Response execute(Request request) throws ActionException {
		if (request instanceof ListEthRequest) {
			return listEths();
		} else if (request instanceof StatEthRequest) {
			return getEthStat((StatEthRequest) request);
		} else if (request instanceof SaveEthRequest) {
			return saveEth((SaveEthRequest) request);
		} else if (request instanceof BondAndUpdateEthRequest) {
			return bondEths((BondAndUpdateEthRequest) request);
		}
		return null;
	}

	/**
	 * Get all Ethernets list.
	 * 
	 * @return returns response including the Ethernets list.
	 */
	private ListEthResponse listEths() {
		ListEthResponse resp = new ListEthResponse();
		try {
			List<Eth> eths = localEthService.findAllEths();
			getEthFunction(eths);
			resp.setEths(eths);
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (ShellException e) {
			resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
		} catch (IOException e) {
			resp.setErrorCode(ActionErrorCode.IO_ERROR);
		}
		return resp;
	}

	/**
	 * Get used rate of the specified Ethernet.
	 * 
	 * @param request - the used rate request
	 * @return returns response including the used rate.
	 */
	private StatEthResponse getEthStat(StatEthRequest request) {
		StatEthResponse resp = new StatEthResponse();
		try {
			resp.setUsedRate(localEthService.getEthUsedRate(request.getEthId()));
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (ShellException e) {
			resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
		} catch (IOException e) {
			resp.setErrorCode(ActionErrorCode.IO_ERROR);
		}
		return resp;
	}

	/**
	 * Save Ethernet settings.
	 * 
	 * @param request - the save request
	 * @return returns response indicating the action is success or not.
	 */
	private SaveEthResponse saveEth(SaveEthRequest request) {
		SaveEthResponse resp = new SaveEthResponse();
		try {
			localEthService.updateEth(request.getEth());
			List<Eth> eths = new ArrayList<Eth>();
			eths.add(request.getEth());
			saveEthFunction(eths);
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (ShellException e) {
			resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
		} catch (IOException e) {
			resp.setErrorCode(ActionErrorCode.IO_ERROR);
		}
		return resp;
	}

	private Eth getBackupEth(List<Eth> allEths, Eth srcEth) {
		Eth ret = null;
		if (srcEth.getMaster() != null) {
			for (Eth e : allEths) {
				if (srcEth.getId().equals(e.getId()))
					continue;
				if (srcEth.getMaster().equals(e.getMaster())) {
					ret = e;
					break;
				}
			}
		}
		return ret;
	}

	private String getBondId(List<Eth> eths) {
		String ret = null;
		for (int i = 0; i < 16; i++) {
			ret = "bond" + i;
			boolean exist = false;
			for (Eth eth : eths) {
				if (eth.getId().equals(ret)) {
					exist = true;
					break;
				}
			}
			if (!exist)
				break;
		}
		return ret;
	}

	/**
	 * Bond Ethernets.
	 * 
	 * @param request - the bond request
	 * @return returns response indicating the action is success or not.
	 */
	private BondAndUpdateEthResponse bondEths(BondAndUpdateEthRequest request) {
		BondAndUpdateEthResponse resp = new BondAndUpdateEthResponse();

		EthRepository ethDao = new EthRepositoryImplRHEL();
		try {
			Eth eth = request.getEth();
			String slaveId = request.getSlaveId();
			List<Eth> eths = ethDao.findAllEths();
			Eth eth1 = null;
			Eth eth2 = null;
			for (Eth theEth : eths) {
				if (theEth.getId().equals(eth.getId())) {
					eth1 = theEth;
				}
				if (slaveId != null && theEth.getId().equals(slaveId)) {
					eth2 = theEth;
				}
			}
			if (eth1 == null || (slaveId != null && eth2 == null)) {
				log.error("can not find the eth");
				throw new IOException("input parameter is error");
			}

			List<Eth> ethsToSaved = new ArrayList<Eth>();
			if (eth1.getMaster() != null) {
				// the eth is already bonded.
				Eth backEth = getBackupEth(eths, eth1);
				if (slaveId == null) {
					// ubound the already bonded.
					Eth master = new Eth(eth1.getMaster());
					ethDao.bond(master, null);
					ethDao.updateEth(eth);
					master.setFunction(null);
					ethsToSaved.add(master);
					ethsToSaved.add(eth);
				} else {
					if (slaveId.equals(backEth.getId())) {
						// update the bond's info.
						eth.setId(eth1.getMaster());
						eth.setIsbond(true);
						ethDao.updateEth(eth);
						ethsToSaved.add(eth);
					} else {
						// ubound the already bounded.
						Eth master = new Eth(eth1.getMaster());
						ethDao.bond(master, null);
						// newly bond
						String slaveId1 = eth.getId();
						eth.setId(getBondId(eths));
						ethDao.bond(eth, new String[] { slaveId1, slaveId });

						master.setFunction(null);
						ethsToSaved.add(master);
						ethsToSaved.add(eth);
					}
				}
			} else {
				if (slaveId == null) {
					ethDao.updateEth(eth);
				} else {
					String slaveId1 = eth.getId();
					eth.setId(getBondId(eths));
					ethDao.bond(eth, new String[] { slaveId1, slaveId });
				}
				ethsToSaved.add(eth);
			}
			saveEthFunction(ethsToSaved);
			resp.setErrorCode(ActionErrorCode.SUCCESS);
		} catch (ShellException e) {
			resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
		} catch (IOException e) {
			resp.setErrorCode(ActionErrorCode.IO_ERROR);
		}
		return resp;
	}

	private void saveEthFunction(List<Eth> eths) throws IOException {
		// Load previous settings from file.
		Properties prop = new Properties();
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
		} else {
			prop = loadSettingsFromFile();
		}

		// Merge the settings.
		for (String function : functions) {
			String ethIds = prop.getProperty(function);
			for (Eth eth : eths) {
				if (eth.getFunction() != null && eth.getFunction().contains(function)
						&& (ethIds == null || !ethIds.contains(eth.getId()))) {
					// add an ethid
					ethIds = (ethIds == null || ethIds.length() == 0) ? eth.getId() : (ethIds + "," + eth.getId());
				} else if (ethIds != null && ethIds.contains(eth.getId())
						&& (eth.getFunction() == null || !eth.getFunction().contains(function))) {
					// remove an ethid
					if (ethIds.startsWith(eth.getId())) {
						if (ethIds.length() == eth.getId().length())
							ethIds = ethIds.replace(eth.getId(), "");
						else
							ethIds = ethIds.replace(eth.getId() + ",", "");
					} else {
						ethIds = ethIds.replace("," + eth.getId(), "");
					}
				}
			}
			if (null != ethIds)
				prop.setProperty(function, ethIds);
		}
		if (listener != null)
			listener.ethSettingsChanged(prop);

		// Save settings to file.
		OutputStream os = null;
		try {
			os = new FileOutputStream(configFile);
			prop.store(os, null);
		} catch (IOException e) {
			log.error("save eth function to file error", e);
			throw e;
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void getEthFunction(List<Eth> eths) {
		try {
			Properties prop = loadSettingsFromFile();
			for (Eth eth : eths) {
				String ethFunction = null;
				for (String function : functions) {
					String ethIds = prop.getProperty(function);
					if (ethIds != null && ethIds.contains(eth.getId())) {
						ethFunction = ethFunction == null ? function : ethFunction + "," + function;
					}
				}
				eth.setFunction(ethFunction);
			}
		} catch (IOException e) {
			log.error("get eth function from file error", e);
		}
	}

	/**
	 * Load settings from the specified file.
	 * 
	 * @return returns the settings in the specified file, or returns empty if file not exits.
	 * @throws java.io.IOException if read the configuration file failed.
	 */
	private Properties loadSettingsFromFile() throws IOException {
		Properties prop = new Properties();
		if (configFile.exists()) {
			InputStream is = null;
			try {
				is = new FileInputStream(configFile);
				prop.load(is);
				return prop;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return prop;
	}

}
