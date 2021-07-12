package com.bookportal.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @SneakyThrows
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String errorMap = getErrorMap(request, response, authException);
        response.getOutputStream().println(errorMap);
    }

    private String getErrorMap(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws Exception {
        String errorResponse;
        LinkedHashMap<String, String> errorMap = new LinkedHashMap<>();
        errorMap.put("error", authException.getMessage());
        errorMap.put("status", String.valueOf(response.getStatus()));
        errorMap.put("path", request.getRequestURI());
        errorMap.put("remote", request.getRemoteAddr());
        errorResponse = new ObjectMapper().writeValueAsString(errorMap);
        log.info(errorMap.toString());
        return errorResponse;
    }
}