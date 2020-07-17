package com.osafe.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * CrossScriptingFilter - Strip Html from all parameters.
 */
public class CrossScriptingFilter implements Filter {
    
    private FilterConfig filterConfig;

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        chain.doFilter(new RequestWrapper((HttpServletRequest) request), response);
    }

    final class RequestWrapper extends HttpServletRequestWrapper {

        public RequestWrapper(HttpServletRequest servletRequest) {
            super(servletRequest);
        }

        public String[] getParameterValues(String parameter) {
            String[] values = super.getParameterValues(parameter);
            if (values==null) {
                return null;
            }
            int count = values.length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++) {
                encodedValues[i] = cleanValue(values[i]);
            }
            return encodedValues;
        }

        public String getParameter(String parameter) {
            String value = super.getParameter(parameter);
            if (value == null) {
                return null;
            }
            return cleanValue(value);
        }

        public String getHeader(String name) {
            String value = super.getHeader(name);
            if (value == null) {
                return null;
            }
            return cleanValue(value);
        }

        private String cleanValue(String value) {
            value = value.replaceAll("<", "").replaceAll(">", "");
            value = value.replaceAll("\\(", "").replaceAll("\\)", "");
            value = value.replaceAll("'", "");
            value = value.replaceAll("eval\\((.*)\\)", "");
            value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
            value = value.replaceAll("(?i)script", "");
            return value;
        }
    }
}