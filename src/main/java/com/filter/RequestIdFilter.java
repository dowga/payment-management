package com.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestIdFilter.class);

    private static final String HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestId = resolveRequestId(
                request.getHeader(HEADER)
        );

        MDC.put("requestId", requestId);

        response.setHeader(HEADER, requestId);

        try {
            log.info(
                    "HTTP request started: {} {}",
                    request.getMethod(),
                    request.getRequestURI()
            );

            filterChain.doFilter(request, response);

        } finally {

            log.info(
                    "HTTP request completed: {} {} status={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus()
            );

            MDC.remove("requestId");
        }
    }

    private String resolveRequestId(String headerValue) {

        if (headerValue != null) {
            try {
                return UUID.fromString(headerValue).toString();
            } catch (IllegalArgumentException ignored) {
                // Generate a new ID for an invalid header.
            }
        }

        return UUID.randomUUID().toString();
    }
}