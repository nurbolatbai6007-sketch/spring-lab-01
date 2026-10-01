package kz.iitu.springlab.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class LoggingAspect {

    @Before("kz.iitu.springlab.aspect.Pointcuts.serviceOperation()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("[BEFORE] " + joinPoint.getSignature().toShortString()
                + " args=" + java.util.Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(
            pointcut = "kz.iitu.springlab.aspect.Pointcuts.serviceOperation()",
            returning = "result"
    )
    public void logReturning(JoinPoint joinPoint, Object result) {
        System.out.println("[RETURN] " + joinPoint.getSignature().toShortString()
                + " result=" + result);
    }

    @AfterThrowing(
            pointcut = "kz.iitu.springlab.aspect.Pointcuts.serviceOperation()",
            throwing = "ex"
    )
    public void logThrowing(JoinPoint joinPoint, Throwable ex) {
        System.out.println("[THROW] " + joinPoint.getSignature().toShortString()
                + " exception=" + ex.getClass().getSimpleName()
                + ": " + ex.getMessage());
    }
}