package com.arcsoft.supervisor.service.audit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Default implementation to convert the {@code Properties} to a {@code ImmutableMap}.
 *
 * @author zw.
 */
@Service
public class DefaultAuditFormatterExpression implements AuditFormatterExpression {

    /**
     * Holds all of expressions as key-value pair.
     */
    private ImmutableMap<String, String> expressions;

    @Autowired
    @Qualifier("auditFormatExpressionProperties")
    @Override
    public void setExpressions(Properties pros) {
        Preconditions.checkNotNull(pros, "The pros can not be null");
        expressions = Maps.fromProperties(pros);
    }

    @Override
    public ImmutableMap<String, String> getExpressions() {
        return expressions;
    }

}
