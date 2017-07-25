package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.NTPStatus;
import com.arcsoft.supervisor.repository.settings.TimeRepository;
import com.arcsoft.supervisor.service.settings.LocalDateTimeService;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * The implementation of LocalDateTimeService
 * 
 * @author xpeng
 * @author zw
 */
@Service
public class LocalDateTimeServiceImpl implements LocalDateTimeService {

    @Autowired
	private TimeRepository timeRepository;

	@Override
	public List<String> listTimeZone(String main) throws ShellException, IOException {
		return timeRepository.listTimeZone(main);
	}

	@Override
	public void setTimezone(String timezone) throws ShellException, IOException {
		timeRepository.setTimezone(timezone);
	}

	@Override
	public void setSystemTime(Date date) throws ShellException, IOException {
		timeRepository.setSystemTime(date);
	}

	@Override
	public void syncWithNTP(NTPStatus ntp) throws ShellException, IOException {
		timeRepository.syncWithNTP(ntp);
	}

	@Override
	public String getTimezone() throws ShellException, IOException {
		return timeRepository.getTimezone();
	}

	@Override
	public NTPStatus getNTPStatus() throws ShellException, IOException {
		return timeRepository.getNTPStatus();
	}

}
