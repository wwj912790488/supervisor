package com.arcsoft.supervisor.model.domain.settings;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * A storage entity class contains some config parameters.
 *
 * @author zw
 * 
 */
@Entity
@Table(name = "storage")
public class Storage{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlElement
	private Integer id;
	@XmlElement	
	private String name;
	@XmlElement
	private String path;
	@XmlElement
	private String type;
	@XmlElement
	private String user;
	@XmlElement
	private String pwd;

    @Transient
    private Boolean mounted = false;

	public Storage() {
	}

	public Storage(int id, String name, String path, String user, String pwd) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.user = user;
		this.pwd = pwd;
	}

	@XmlTransient
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null)
			this.name = name.trim();
	}

	@XmlTransient
	public String getPath() {
		return path;
	}

	@XmlTransient
	public String getUser() {
		return user;
	}

	public void setPath(String path) {
		if (path != null)
			path = path.trim().replace('\\', '/');
		this.path = path;
	}

	public void setUser(String user) {
		if (user != null)
			this.user = user.trim();
	}

	public void setPwd(String pwd) {
		if (pwd != null)
			this.pwd = pwd.trim();
	}

	@XmlTransient
	public String getPwd() {
		return pwd;
	}

	@XmlTransient
	public String getType() {
		return (type == null || type.length() == 0) ? "cifs" : type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public boolean equals(Storage storage) {
		return storage != null && id.equals(storage.getId()) && name.equals(storage.getName())
				&& path.equals(storage.getPath()) && user.equals(storage.getUser()) && pwd.equals(storage.getPwd())
				&& type.equals(storage.getType());
	}

    public Boolean getMounted() {
        return mounted;
    }

    public void setMounted(Boolean mounted) {
        this.mounted = mounted;
    }

    @Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Storage{");
		sb.append("id=").append(id);
		sb.append(", name='").append(name).append('\'');
		sb.append(", path='").append(path).append('\'');
		sb.append(", type='").append(type).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
