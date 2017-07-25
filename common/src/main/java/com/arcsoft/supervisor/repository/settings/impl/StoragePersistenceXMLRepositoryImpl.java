package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.repository.settings.StoragePersistenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Xml file persistence implement to crud the storage with a xml file.
 *
 * @author hxiang
 * @author zw
 */
public class StoragePersistenceXMLRepositoryImpl implements StoragePersistenceRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(StoragePersistenceXMLRepositoryImpl.class);

	private String path = "./storages.xml";

	private Map<Integer, Storage> storages = new ConcurrentHashMap<>();

    private final Lock increaseLock;

    public StoragePersistenceXMLRepositoryImpl() {
        this.increaseLock = new ReentrantLock();
    }

    @Override
	public Storage get(Integer id) {
		return storages.get(id);
	}

	@Override
	public List<Storage> get() {
		List<Storage> list = new ArrayList<>();
		for (Storage each : storages.values())
			list.add(each);
		return list;
	}

	@Override
	public Integer save(Storage storage) {
        increaseLock.lock();
        try {
            storage.setId(autoIncreaseId());
        }finally {
            increaseLock.unlock();
        }
        storages.put(storage.getId(), storage);
        if (!syn()) {
			storages.remove(storage.getId());
			storage.setId(-1);
		}
		return storage.getId();
	}

    private int autoIncreaseId() {
        int index = 0;
        for (Integer each : storages.keySet()) {
            if (each > index)
                index = each;
        }
        return index + 1;
    }

	@Override
	public void delete(Integer id) {
		storages.remove(id);
		syn();
	}

	@Override
	public boolean update(Storage storage) {
		boolean ret = false;
		if (storages.containsKey(storage.getId())) {
			Storage st = storages.get(storage.getId());
			st.setId(storage.getId());
			st.setType(storage.getType());
			st.setName(storage.getName());
			st.setPath(storage.getPath());
			st.setUser(storage.getUser());
			st.setPwd(storage.getPwd());
			syn();
			ret = true;
		}
		return ret;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void initialize() {
		try {
			File file = new File(path).getAbsoluteFile();
			if (file.exists()) {
				JAXBContext jaxbContext = JAXBContext.newInstance(StorageList.class);
				Unmarshaller marshaller = jaxbContext.createUnmarshaller();
				StorageList list = (StorageList) marshaller.unmarshal(file);

				for (Storage storage : list.getList()) {
					storages.put(storage.getId(), storage);
				}
			}

		} catch (JAXBException e) {
			LOGGER.error("Failed to load storages from " + path, e);
		}
	}

	private synchronized boolean syn() {
		boolean ret = false;
		try {
			File file = new File(path).getAbsoluteFile();

			File parentDir = new File(file.getParent());
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			JAXBContext jaxbContext = JAXBContext.newInstance(StorageList.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StorageList storageList = new StorageList();
			storageList.setList(storages.values());
			marshaller.marshal(storageList, file);
			ret = true;
		} catch (JAXBException e) {
			// Do nothing.
		} catch (IOException e) {
			// Do nothing here.
		} catch (Exception e) {
		}
		return ret;
	}

	@XmlRootElement(name = "root")
	@XmlAccessorType(XmlAccessType.PROPERTY)
	private static class StorageList {

		@XmlElementWrapper(name = "storages")
		@XmlElement(name = "storage")
		private Collection<Storage> list = new ArrayList<>();

		@XmlTransient
		public Collection<Storage> getList() {
			return list;
		}

		public void setList(Collection<Storage> list) {
			this.list = list;
		}
	}
}
