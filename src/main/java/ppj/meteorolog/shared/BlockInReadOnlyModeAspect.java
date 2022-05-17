package ppj.meteorolog.shared;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BlockInReadOnlyModeAspect {

    @Value("${app.isReadOnly}")
    private boolean isReadOnly;

    @Around("@annotation(BlockInReadOnlyMode)")
    public Object BlockInReadOnlyMode(ProceedingJoinPoint joinPoint) throws Throwable {
        if(!isReadOnly)
            return joinPoint.proceed();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        if (ResponseEntity.class.equals(signature.getReturnType()))
            return ResponseEntity.status(403).build();
        else
            return null;
    }
}