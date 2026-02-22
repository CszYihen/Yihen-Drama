package com.yihen.aop;


import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
/**
 *  日志切面，统一记录打印日志
 *
 * */
public class LogAspect {
    /** 切点：Controller 层所有方法 */
    @Pointcut("execution(* com.yihen.controller..*(..))")
    public void controllerPointcut(){}

    /** 环绕通知*/
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始时间
        long start = System.currentTimeMillis();

        // 获取方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取方法上的Operation注解
        Operation operation = method.getAnnotation(Operation.class);

        // 方法名
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String name = method.getName();

        // 参数
        Object[] args = joinPoint.getArgs();

        log.info("{} -> 进入方法: {}.{}()，参数: {}",
                operation.summary(),className,name, Arrays.toString(args));
        Object result;

        try {
            // 执行业务方法
             result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("{} 方法异常: {}.{}()，异常信息{}",
                    operation.summary(),className, name, e.getMessage(),e);

            throw e;
        }

        // 方法花费时间
        long cost = System.currentTimeMillis() - start;
        log.info("{} <- 方法返回: {}.{}()，返回值: {}，耗时: {} ms",
                operation.summary(),className, name,result, cost);


        return result;
    }
}
