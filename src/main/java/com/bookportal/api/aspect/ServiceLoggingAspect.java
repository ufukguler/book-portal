package com.bookportal.api.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    @Before("execution(* com.bookportal.api.service.*.*(..))")
    public void logAction(JoinPoint joinPoint) {
        String[] split = joinPoint.getSignature().getDeclaringTypeName().split("\\.");
        final String path = split[split.length - 1] + "." + joinPoint.getSignature().getName();
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        List<String> params = new ArrayList<>();
        List<String> values = new ArrayList<>();
        int length = joinPoint.getArgs().length;
        for (int i = 0; i < length; i++) {
            params.add(codeSignature.getParameterNames()[i]);
            values.add(joinPoint.getArgs()[i].toString());

        }
        log.info(path + " Input parameters are: [" + String.join(",", params) + "] values are: [" + String.join(",", values) + "]");

    }

    @AfterReturning(pointcut = "execution(* com.bookportal.api.service.*.*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String[] split = joinPoint.getSignature().getDeclaringTypeName().split("\\.");
        final String path = split[split.length - 1] + "." + joinPoint.getSignature().getName();
        if (result instanceof PageImpl) {
            StringBuilder sb = new StringBuilder();
            ((PageImpl<?>) result).getContent().forEach(o -> sb.append(o.toString()).append(" "));
            log.info(path + ". Return value is: [ " + sb + " ]");
        } else {
            if (result != null) {
                log.info(path + ". Return value is: [ " + result + " ]");
            }
        }

    }

}
