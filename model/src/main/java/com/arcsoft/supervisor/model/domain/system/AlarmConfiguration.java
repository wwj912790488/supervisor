package com.arcsoft.supervisor.model.domain.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Entity class for <code>Alarm</code> of Configuration.
 *
 * @author jt.
 */
@Entity
@Table(name = "configuration_alarm")
@DiscriminatorValue("7")
@DynamicUpdate
public class AlarmConfiguration extends Configuration {

    private String androidapikey;
    private String androidsecretkey;
    private String iosapikey;
    private String iossecretsey;

    public AlarmConfiguration() {
    }

    public String getAndroidapikey() {
        return androidapikey;
    }

    public void setAndroidapikey(String androidapikey) {
        this.androidapikey = androidapikey;
    }

    public String getAndroidsecretkey() {
        return androidsecretkey;
    }

    public void setAndroidsecretkey(String androidsecretkey) {
        this.androidsecretkey = androidsecretkey;
    }

    public String getIosapikey() {
        return iosapikey;
    }

    public void setIosapikey(String iosapikey) {
        this.iosapikey = iosapikey;
    }

    public String getIossecretsey() {
        return iossecretsey;
    }

    public void setIossecretsey(String iossecretsey) {
        this.iossecretsey = iossecretsey;
    }

}
