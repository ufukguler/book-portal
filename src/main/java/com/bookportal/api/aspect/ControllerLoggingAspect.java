package com.bookportal.api.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Aspect
@Component
public class ControllerLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    @Before("execution(* com.bookportal.api.controllers.*.*(..))")
    public void logAction(JoinPoint joinPoint) {
        String[] controller = joinPoint.getSignature().getDeclaringTypeName().split("\\.");
        final String path = controller[controller.length - 1] + "." + joinPoint.getSignature().getName();
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        List<String> params = new ArrayList<>();
        List<String> values = new ArrayList<>();
        int length = joinPoint.getArgs().length;
        for (int i = 0; i < length; i++) {
            params.add(codeSignature.getParameterNames()[i]);
            values.add(joinPoint.getArgs()[i].toString());

        }
        log.info(path + ". Input parameters are: [" + String.join(",", params) + "] values are: [" + String.join(",", values) + "]");
    }
}
