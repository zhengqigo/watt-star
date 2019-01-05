package org.fuelteam.watt.star.annotation;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.fuelteam.watt.star.core.Proxy;
import org.fuelteam.watt.star.core.Proxy.SwitchExecute;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MasterSlaveAspect {

    @Around(value = "@annotation(org.fuelteam.watt.star.annotation.MasterSlaveRouter)", argNames = "point")
    public Object doAround(final ProceedingJoinPoint jointPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) jointPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> targetClass = jointPoint.getTarget().getClass();
        Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
        MasterSlaveRouter router = AnnotationUtils.findAnnotation(targetMethod, MasterSlaveRouter.class);
        if (router == null) return jointPoint.proceed();
        SwitchExecute<Object> execute = new SwitchExecute<Object>() {
            @Override
            public Object run() throws Throwable {
                return jointPoint.proceed();
            }
        };
        if (router.slave()) return Proxy.slave(execute);
        return Proxy.master(execute);
    }
}
