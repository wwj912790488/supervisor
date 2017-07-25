package com.arcsoft.supervisor.model.domain.system;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * Entity class for <code>Gpu</code> of configuration.
 *
 * @author zw.
 */
@Entity
@Table(name = "configuration_gpu")
@DiscriminatorValue("3")
@DynamicUpdate
public class GpuConfiguration extends Configuration{

    /**
     * Enable or disable span gpu.
     */
    private Boolean enableSpan;

    public boolean isEnableSpan() {
        return enableSpan == null ? true : enableSpan;
    }

    public void setEnableSpan(boolean enableSpan) {
        this.enableSpan = enableSpan;
    }
}
