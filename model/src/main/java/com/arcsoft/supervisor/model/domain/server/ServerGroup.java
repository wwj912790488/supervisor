package com.arcsoft.supervisor.model.domain.server;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * Server group.
 * 
 * @author fjli
 * @author zw
 */
@Entity
@Table(name = "server_groups")
public class ServerGroup implements Serializable {

	private static final long serialVersionUID = 6091025107750794156L;

	/**
	 * Indicate this group is default group.
	 */
	public static final int TYPE_DEFAULT = 0;

	/**
	 * Indicate this group is 1+1 backup group.
	 */
	public static final int TYPE_LIVE = 1;

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
    @Column(name = "group_name", unique = true, nullable = false)
	private String name;
    @Column(length = 1, nullable = false, name = "group_type")
	private Integer type;
    @Transient
	private transient List<Server> servers;

	/**
	 * Return the group id.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Set the group id.
	 * 
	 * @param id - the group id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the group name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the group name.
	 * 
	 * @param name - the group name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the group type.
	 * 
	 * @see #TYPE_DEFAULT
	 * @see #TYPE_LIVE
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * Set the group type.
	 * 
	 * @param type - the group type
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * Returns the servers.
	 */
	public List<Server> getServers() {
		return servers;
	}

	/**
	 * Set the servers belongs to this group.
	 * 
	 * @param servers - the servers to be set
	 */
	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

}
