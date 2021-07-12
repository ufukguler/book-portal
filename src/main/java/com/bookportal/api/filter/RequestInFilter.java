package com.bookportal.api.filter;

import com.bookportal.api.model.RequestFilterLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class RequestInFilter extends CommonsRequestLoggingFilter implements Filter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_METHODS, "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_HEADERS, "POST, GET, PUT, DELETE, OPTIONS, PATCH, HEAD");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        String pathInfo = request.getRequestURI();
        return pathInfo.contains("/api/");
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        String username = request.getUserPrincipal() == null ? "" : request.getUserPrincipal().getName();
        log.info("beforeRequest: " + new RequestFilterLogDTO(username,
                request.getRequestURI(), request.getMethod(), message, request.getRemoteAddr()));
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        String username = request.getUserPrincipal() == null ? "" : request.getUserPrincipal().getName();
        log.info("afterRequest: " + new RequestFilterLogDTO(username,
                request.getRequestURI(), request.getMethod(), message, request.getRemoteAddr()));
    }
}
