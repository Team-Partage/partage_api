package com.egatrap.partage.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
@Profile("local")
public class LoggerAspect {

    @Pointcut("execution(* com.egatrap.*..*.*(..)) " +
            "&& !execution(* com.egatrap.*.common..*.*(..)) " +
            "&& !execution(* com.egatrap.*.security..*.*(..)) "
    )
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object loggerAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String type = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        log.debug("### ->> {}.{} ###", type, methodName);
        Object obj = joinPoint.proceed();
        log.debug("### <<- {}.{} ###", type, methodName);

        return obj;
    }

}
