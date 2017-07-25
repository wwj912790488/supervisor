package com.arcsoft.supervisor.web;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A filter implementation to do security validate.
 *
 * @author zw.
 */
public class SecurityFilter implements Filter {

    private List<Pattern> patterns;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludePatterns = filterConfig.getInitParameter("excludePatterns");
        String[] excludePatternArr = excludePatterns.split(",");
        this.patterns = new ArrayList<>();
        for (String ep : excludePatternArr) {
            this.patterns.add(Pattern.compile(ep));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (isExclude(httpServletRequest.getRequestURI().replaceFirst(httpServletRequest.getContextPath(), ""))) {
            chain.doFilter(request, response);
        } else {
            Object userObj = httpServletRequest.getSession().getAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO);
            if (userObj == null) {
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login/index");
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        patterns.clear();
    }

    private boolean isExclude(final String path) {
        return FluentIterable.from(this.patterns).firstMatch(new Predicate<Pattern>() {
            @Override
            public boolean apply(Pattern input) {
                return input.matcher(path).find();
            }
        }).isPresent();
    }
}
