package com.arcsoft.supervisor.service.audit;

import com.google.common.collect.ImmutableMap;

import java.util.Properties;

/**
 * A Expression providers expressions to do format with {@code AuditFormatter}.
 *
 * @author zw.
 */
public interface AuditFormatterExpression {

    /**
     * Sets the expressions.
     *
     * @param pros the object holds all of expressions
     * @throws NullPointerException if the props is null
     */
    public void setExpressions(Properties pros);

    /**
     * Retrieves all of expressions by loaded resource.
     *
     * @return the expression used for format
     */
    public ImmutableMap<String, String> getExpressions();

}
