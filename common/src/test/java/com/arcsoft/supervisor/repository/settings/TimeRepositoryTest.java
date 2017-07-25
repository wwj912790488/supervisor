package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.settings.NTPStatus;
import com.arcsoft.supervisor.repository.settings.impl.TimeRepositoryImplRHEL;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for time setting
 * 
 * @author xpeng
 * 
 */
public class TimeRepositoryTest {
	final boolean RUN_ON_RHEL = false;
	private TimeRepository timeRepository;

	@Before
	public void setUp() {
		timeRepository = new TimeRepositoryImplRHEL();
	}

	@Test
	public void testListTimeZone() throws ShellException, IOException {
		if (RUN_ON_RHEL) {
			List<String> zoneList = timeRepository.listTimeZone("Asia");
			assertTrue(zoneList.size() > 0);
			assertTrue(zoneList.contains("Shanghai"));
		}
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testSetTimezone() throws ShellException, IOException {
		if (RUN_ON_RHEL) {
			String expect = "Asia/Tokyo"; // UTC+9
			timeRepository.setTimezone(expect);

			Date actual = new Date();// get current time
			int actualZone = -(actual.getTimezoneOffset()/60);			
			assertEquals(9, actualZone);	
		}
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testSetSystemTime() throws ShellException, IOException {
		if (RUN_ON_RHEL) {
			Date old = new Date();
			
			Date expect = new Date();			
			expect.setYear(120);
			expect.setHours(5);
			timeRepository.setSystemTime(expect);

			Date actual = new Date();// get current time
			assertEquals(expect.getYear(), actual.getYear());
			assertEquals(expect.getHours(), actual.getHours());
			
			timeRepository.setSystemTime(old);
		}
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testSyncWithNTP() throws ShellException, IOException {
		if (RUN_ON_RHEL) {
			Date expect = new Date();
			expect.setYear(120);
			timeRepository.setSystemTime(expect);

			Date actual = new Date();// get current time
			assertEquals(expect.getYear(), actual.getYear());

			List<String> servers = new ArrayList<String>();
			servers.add("172.17.186.90");
			servers.add("172.17.186.91");
			NTPStatus expectNTP = new NTPStatus(true, servers);
			
			timeRepository.syncWithNTP(expectNTP);

			actual = new Date();// get current time
			assertEquals(113, actual.getYear()); //2013-1900=113
			
			//test getNTPStatus
			NTPStatus actualNTP = timeRepository.getNTPStatus();
			assertEquals(true, actualNTP.getIsServiceOn());
			assertEquals(2, actualNTP.getNtpServers().size());
			assertEquals("172.17.186.90", actualNTP.getNtpServers().get(0));
			assertEquals("172.17.186.91", actualNTP.getNtpServers().get(1));
		}
	}

}
