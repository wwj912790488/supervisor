package com.arcsoft.supervisor.commons.spring;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource manager.
 * 
 * @author fjli
 */
public class ResourcesManager {

	private Logger log = Logger.getLogger(getClass());
	private List<Resource> resources = new ArrayList<>();
	private List<Resource> excluded = new ArrayList<>();

	/**
	 * Set resources locations.
	 * 
	 * @param resources
	 */
	public void setResources(Resource[] resources) {
		for (Resource resource : resources) {
			if (!this.resources.contains(resource)) {
				this.resources.add(resource);
			}
		}
	}

	/**
	 * Set excluded locations.
	 * 
	 * @param excludedLocations
	 */
	public void setExcluded(Resource[] excludedLocations) {
		for (Resource resource : excludedLocations) {
			if (!this.excluded.contains(resource))
				this.excluded.add(resource);
		}
	}

	/**
	 * Returns all included resources.
	 */
	public Resource[] getResources() {
		if (excluded.isEmpty())
			return this.resources.toArray(new Resource[0]);
		List<Resource> results = new ArrayList<>();
		for (Resource resource : resources) {
			if (excluded.contains(resource)) {
				log.info("resource excluded: " + resource);
				continue;
			}
			results.add(resource);
		}
		return results.toArray(new Resource[0]);
	}

}
