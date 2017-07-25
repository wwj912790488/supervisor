package com.arcsoft.supervisor.service.audit;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * A base implementation of {@code AuditContent} defines some commons properties.
 * <p>You should extends this class if you have some additional properties.
 *
 * @author zw.
 */
public class BaseAuditContent implements AuditContent {

    private final AuditLevel level;
    private String description;
    private SupervisorDefs.Modules module;
    private final Date createdTime;

    public BaseAuditContent(Date createdTime, SupervisorDefs.Modules module, String description, AuditLevel level) {
        this.createdTime = createdTime;
        this.module = module;
        this.description = description;
        this.level = level;
    }

    @Override
    public AuditLevel getLevel() {
        return level;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setModule(SupervisorDefs.Modules module) {
        this.module = module;
    }

    @Override
    public SupervisorDefs.Modules getModule() {
        return module;
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("level", level)
                .add("description", description)
                .add("module", module)
                .add("createdTime", createdTime);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

}
