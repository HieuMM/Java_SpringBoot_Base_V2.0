package com.vudt.login.configs.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Aspect
@Configuration
public class AspectConfiguration {
    @Around("execution(* com.vudt.login.services.*.*(org.springframework.data.domain.Pageable, ..))") // pointcut expression only apply with these methods service has first parameter type is Pageable
    public Object fixPageIfExceed(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs(); // get arguments
        Pageable page = (Pageable) args[0]; // get pageable object
        page = page.getPageSize() > 50 ? PageRequest.of(page.getPageNumber(), 50).withSort(page.getSort()) : page; // set page size to 50 if page size is greater than 50
        args[0] = page; // set pageable object back to args
        return joinPoint.proceed(args); //continue to proceed to method
    }

}
