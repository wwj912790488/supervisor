package com.arcsoft.supervisor.model.domain.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * System setting entity.
 * 
 * @author fjli
 */
@Entity
@Table(name = "settings")
public class SystemSettingEntity {

    @Id
    @Column(name = "`key`")
	private String key;
	private String value;

	/**
	 * Returns the setting key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set the setting key.
	 * 
	 * @param key - the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Returns the setting value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the setting value.
	 * 
	 * @param value - the value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
