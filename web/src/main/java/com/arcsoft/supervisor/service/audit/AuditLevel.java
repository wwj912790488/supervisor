package com.arcsoft.supervisor.service.audit;

import com.google.common.base.MoreObjects;

/**
 * The AuditLevel class defines a set of logging level.
 *
 * @author zw.
 */
public class AuditLevel {

    /**
     * The name of the AuditLevel.
     */
    private final String name;

    /**
     * The integer value of the AuditLevel.
     */
    private final int value;

    /**
     * Defines error level.
     */
    public static final AuditLevel ERROR = new AuditLevel("ERROR", 10);

    /**
     * Defines warn level.
     */
    public static final AuditLevel WARN = new AuditLevel("WARN", 9);


    protected AuditLevel(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("value", value)
                .toString();
    }
}
