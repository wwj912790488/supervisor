package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.settings.Eth;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.io.IOException;
import java.util.List;

/**
 * Dao for Eth. Remove DNS, route from this class on 2013-07-24 by xpeng
 * 
 * @author Bing
 * @author xpeng
 * 
 */
public interface EthRepository {

	/**
	 * List all eths
	 * 
	 * @return the eth list
	 * @throws ShellException
	 *             if shell run error in linux os.
	 * @throws java.io.IOException
	 *             if I/O operation run error
	 */
	List<Eth> findAllEths() throws ShellException, IOException;

	/**
	 * Update one eth
	 * 
	 * @param eth
	 *            the specific eth
	 * @throws ShellException
	 *             if shell run error in linux os.
	 * @throws java.io.IOException
	 *             if I/O operation run error
	 */
	void updateEth(Eth eth) throws ShellException, IOException;

	/**
	 * Get the specific eth's used rate
	 * 
	 * @param ethId
	 *            the specific eth's id
	 * @return (val*10000)
	 * @throws ShellException
	 *             if shell run error in linux os.
	 * @throws java.io.IOException
	 *             if I/O operation run error
	 */
	int getEthUsedRate(String ethId) throws ShellException, IOException;

	/**
	 * Bond the slaves to a virual interface
	 * 
	 * @param master
	 *            the master to bond
	 * @param slaveEthId
	 *            the slaves to bond， if it is null, will unbond the master
	 * @throws ShellException
	 *             if shell run error in linux os.
	 * @throws java.io.IOException
	 *             if I/O operation run error
	 */
	void bond(Eth master, String[] slaveEthId) throws IOException,
			ShellException;

}
