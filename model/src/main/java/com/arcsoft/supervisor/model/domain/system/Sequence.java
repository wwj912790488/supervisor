package com.arcsoft.supervisor.model.domain.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class for sequence.
 *
 * @author zw.
 */
@Entity
@Table(name = "sequence")
public class Sequence {

    /**
     * Sequence key used for send message to mobile.
     */
    public static final String KEY_SMS_ID = "sms_id";

    @Id
    @Column(name = "`key`")
    private String key;

    @Column(name = "`value`")
    private long value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
