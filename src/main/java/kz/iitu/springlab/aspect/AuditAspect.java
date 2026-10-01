package kz.iitu.springlab.aspect;

import kz.iitu.springlab.audit.Audited;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Order(1)
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint pjp, Audited audited) throws Throwable {
        String action = audited.action();
        String timestamp = LocalDateTime.now().toString();

        try {
            Object result = pjp.proceed();

            if (audited.logArguments()) {
                log.info("[AUDIT] action={} time={} outcome=success args={}",
                        action, timestamp, java.util.Arrays.toString(pjp.getArgs()));
            } else {
                log.info("[AUDIT] action={} time={} outcome=success",
                        action, timestamp);
            }

            return result;

        } catch (Throwable ex) {
            log.error("[AUDIT] action={} time={} outcome=failure error={}",
                    action, timestamp, ex.getMessage());

            throw ex;
        }
    }
}