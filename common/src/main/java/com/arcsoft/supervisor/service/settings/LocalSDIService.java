package com.arcsoft.supervisor.service.settings;

import java.util.List;

public interface LocalSDIService {
	public List<String> list();
	public void recognize(String name, int number);
}
