package com.arcsoft.supervisor.repository.server.impl;

import com.arcsoft.supervisor.exception.ObjectNotExistsException;
import com.arcsoft.supervisor.exception.server.NameExistsException;
import com.arcsoft.supervisor.model.domain.server.ServerGroup;
import com.arcsoft.supervisor.repository.server.ServerGroupRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


/**
 * The implementation class for {@link ServerGroupRepository}.
 * 
 * @author fjli
 */
@Repository
public class ServerGroupRepositoryImpl implements ServerGroupRepository {

    @PersistenceContext
    private EntityManager em;

	@Override
    @SuppressWarnings("unchecked")
	public List<ServerGroup> list() {
		return em.createQuery("select g from ServerGroup g").getResultList();
	}

	@Override
	public void createGroup(ServerGroup group) throws NameExistsException {
		em.persist(group);
	}

	@Override
	public void deleteGroup(ServerGroup group) {
		em.remove(group);
	}

	@Override
	public void renameGroup(ServerGroup group) throws ObjectNotExistsException, NameExistsException {
        Query query = em.createQuery("update ServerGroup set name=:name where id=:id")
                .setParameter("name", group.getName())
                .setParameter("id", group.getId());
        if (query.executeUpdate() < 1){
            throw new ObjectNotExistsException(group);
        }
	}

	@Override
	public ServerGroup getGroup(Integer id) {
		return em.find(ServerGroup.class, id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isExistsGroupName(String name) {
        List groups = em.createQuery("select g from ServerGroup g where name = :name").setParameter("name", name).getResultList();
		return groups.size() > 0;
	}

}
