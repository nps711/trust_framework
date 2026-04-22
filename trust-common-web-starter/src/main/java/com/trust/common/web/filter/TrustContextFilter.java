package com.trust.common.web.filter;

import com.trust.common.core.context.UserContext;
import com.trust.common.core.context.UserContextHolder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.UUID;

public class TrustContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String userId = httpServletRequest.getHeader("x-user-id");
        String traceId = httpServletRequest.getHeader("x-trace-id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        try {
            UserContextHolder.setContext(new UserContext(userId, traceId));
            chain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }
}
