package com.arcsoft.supervisor.repository.system.impl;

import com.arcsoft.supervisor.model.domain.system.SystemSettingEntity;
import com.arcsoft.supervisor.repository.system.SystemRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Set;



/**
 * Implementation class for SystemDao.
 * 
 * @author fjli
 * @author zw
 */
@Repository
public class SystemRepositoryImpl implements SystemRepository {

    @PersistenceContext
    private EntityManager em;

	@Override
    @SuppressWarnings("unchecked")
    public HashMap<String, String> getSettings() {
		HashMap<String, String> properites = new HashMap<String, String>();
		List<SystemSettingEntity> list = em.createQuery("select setting from SystemSettingEntity as setting").getResultList();
		for (SystemSettingEntity entity : list) {
			properites.put(entity.getKey(), entity.getValue());
		}
		return properites;
	}

	@Override
	public void saveSettings(HashMap<String, String> settings) {
		Set<String> keys = settings.keySet();
		for (String key : keys) {
			SystemSettingEntity entity = new SystemSettingEntity();
			entity.setKey(key);
			entity.setValue(settings.get(key));
			em.merge(entity);
		}
	}

	@Override
	@Transactional
	public void autoDeleteContentLogs(long endTime) {
		em.joinTransaction();
		em.createQuery("delete from ContentDetectLog as u  where u.endTime<="+endTime).executeUpdate();
		em.flush();
	}


}
