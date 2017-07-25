package com.arcsoft.supervisor.repository.server.impl;

import com.arcsoft.supervisor.exception.ObjectAlreadyExistsException;
import com.arcsoft.supervisor.exception.ObjectNotExistsException;
import com.arcsoft.supervisor.exception.server.NameExistsException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerType;
import com.arcsoft.supervisor.repository.server.ServerRepository;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


/**
 * The implementation for {@link ServerRepository}.
 *
 * @author zw
 */
public class ServerJpaRepositoryImpl implements ServerRepository {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    @Override
    public List<Server> listAll() {
        return em.createQuery("select s from Server s").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Server> listByType(ServerType type) {
        return em.createQuery("select s from Server s where s.type = :type")
                .setParameter("type", type.getValue()).getResultList();
    }

    @Override
    public Server getServer(String id) {
        return em.find(Server.class, id);
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public boolean isExistsServerName(String name) {
        List servers = em.createQuery("select s from Server s where name = :name").setParameter("name", name).getResultList();
        return servers.size() > 0;
    }

    @Override
    public void addServer(Server server) throws ObjectNotExistsException, ObjectAlreadyExistsException {
        try {
            em.persist(server);
        } catch (DataIntegrityViolationException e) {
            Throwable t = e.getMostSpecificCause();
            String message = t.getMessage();
            if (message != null) {
                if (message.contains("server_name")) {
                    throw new NameExistsException(server.getName());
                }
            }
            throw new ObjectAlreadyExistsException(server);
        }
    }


    @Override
    public void renameServer(Server server) throws ObjectNotExistsException, NameExistsException {
        Query query = em.createQuery("update Server set name = :name where id = :id")
                .setParameter("name", server.getName())
                .setParameter("id", server.getId());
        if (query.executeUpdate() < 1) {
            throw new ObjectNotExistsException(server);
        }
    }

    @Override
    public void updateState(Server server) throws ObjectNotExistsException {
        Query query = em.createQuery("update Server set state = :state where id = :id")
                .setParameter("state", server.getState())
                .setParameter("id", server.getId());
        if (query.executeUpdate() < 1) {
            throw new ObjectNotExistsException(server);
        }
    }

    @Override
    public void updateAddress(Server server) throws ObjectNotExistsException {
        Query query = em.createQuery("update Server set ip=:ip, port=:port where id=:id")
                .setParameter("ip", server.getIp())
                .setParameter("port", server.getPort())
                .setParameter("id", server.getId());
        if (query.executeUpdate() < 1) {
            throw new ObjectNotExistsException(server);
        }
    }

    @Override
    public void updateOnlineState(Server server) throws ObjectNotExistsException {
        Integer gpus=(server.getGpus()!=null && server.getGpus()>0)?server.getGpus():0;
        Query query = em.createQuery("update Server set alive=:alive,gpus=:gpus where id=:id")
                .setParameter("alive", server.isAlive())
                .setParameter("gpus",gpus)
                .setParameter("id", server.getId());
        if (query.executeUpdate() < 1) {
            throw new ObjectNotExistsException(server);
        }
    }

    @Override
    public void removeServer(Server server) {
        em.createQuery("delete from Server where id=:id").setParameter("id", server.getId()).executeUpdate();
    }

    @Override
    public void resetServersStatus() {
        em.createQuery("update Server set alive=:isAlived").setParameter("isAlived", false).executeUpdate();
    }

}
