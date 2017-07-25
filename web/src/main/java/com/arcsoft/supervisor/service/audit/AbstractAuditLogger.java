package com.arcsoft.supervisor.service.audit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 * This class providers a skeletal implementation of the {@code AuditLogger} interface to minimize
 * the effort required to implement this interface.
 *
 * @author zw.
 */
public abstract class AbstractAuditLogger<T extends AuditContent> implements AuditLogger<T> {

    /**
     * The expression that will be used to format the log.
     */
    private AuditFormatterExpression expression;
    /**
     * The expression parser object.
     */
    private ExpressionParser expressionParser;
    /**
     * The ParserContext to customize the expression symbol.
     */
    private ParserContext parserContext;

    /**
     * A regex pattern to decision continue parse expression or not.
     */
    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^{^}^\\\\]+)\\}");


    /**
     * Sets the parserContext used for {@link #expressionParser}.
     *
     * @param parserContext
     */
    @Autowired
    public void setParserContext(ParserContext parserContext) {
        this.parserContext = parserContext;
    }

    @Autowired
    @Override
    public void setFormatterExpression(AuditFormatterExpression expression) {
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     * <p>This method will expose a bean of {@code RootObject} which you can used in {@code exp}.The {@code RootObject}
     * contains two field which one is the {@code content} and other is all of {@code expressions} load from {@link
     * AuditFormatterExpression#setExpressions(Properties)}.
     *
     * @param content the content instance to be format
     * @param exp     the expression to be apply to the {@code content}
     * @return the formatted string
     * @see RootObject
     * @see AuditFormatterExpression
     */
    @Override
    public String format(T content, String exp) {
        Preconditions.checkNotNull(expression.getExpressions(), "The expressions can not be null");
        String expressionStr = expression.getExpressions().get(exp);
        Preconditions.checkNotNull(expressionStr, "Can not found the expression with [" + exp + "]");
        String value = expressionParser.parseExpression(expressionStr, parserContext).getValue(new RootObject(content,
                expression.getExpressions())).toString();
        while (PATTERN.matcher(value).find()) {
            value = expressionParser.parseExpression(value, parserContext).getValue(new RootObject(content,
                    expression.getExpressions())).toString();
        }
        return value;
    }

    @Autowired
    @Qualifier("expressionParser")
    @Override
    public void setExpressionParser(ExpressionParser parser) {
        this.expressionParser = parser;
    }

    /**
     * A class to holds the {@code AuditContent} and {@code expressions}.
     */
    private class RootObject {
        private final AuditContent content;
        private final ImmutableMap<String, String> exprs;

        public RootObject(AuditContent content, ImmutableMap<String, String> exprs) {
            this.content = content;
            this.exprs = exprs;
        }

        public AuditContent getContent() {
            return content;
        }

        public ImmutableMap<String, String> getExprs() {
            return exprs;
        }
    }
}
