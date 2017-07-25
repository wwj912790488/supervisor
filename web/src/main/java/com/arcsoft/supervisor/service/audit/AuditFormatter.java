package com.arcsoft.supervisor.service.audit;

import org.springframework.expression.ExpressionParser;

/**
 * A interface to format the audit log.
 *
 * @author zw.
 */
public interface AuditFormatter<T extends AuditContent> {


    /**
     * Sets the {@code expression}.
     *
     * @param expression the expression object contains all of expressions
     */
    public void setFormatterExpression(AuditFormatterExpression expression);

    /**
     * Formats the given {@code content} to a string.
     *
     * @param content the content instance to be format
     * @param exp     the expression to be apply to the {@code content}
     * @return the string after formatted
     */
    public String format(T content, String exp);

    /**
     * Sets the {@code parser} used to parse the expression.
     *
     * @param parser the parser to be used.
     */
    public void setExpressionParser(ExpressionParser parser);

}
