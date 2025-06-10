package com.tenpo.aop;

import com.tenpo.model.HistoryAverage;
import com.tenpo.service.TenpoServiceImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class RequestInfoAspect {

    @Autowired
    private TenpoServiceImpl tenpoServiceImpl;

    @Around("@annotation(SaveRequestInfo)")
    public Object saveHistoryRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;

        HistoryAverage history = HistoryAverage.builder()
                .date(LocalDateTime.now())
                .endpoint(String.valueOf(joinPoint.getSignature()))
                .input(Arrays.toString(joinPoint.getArgs()))
                .build();

        try {
            result = joinPoint.proceed();
            history.setOutput(result.toString());
            tenpoServiceImpl.saveHistoryAverage(history);

            return result;

        } catch (Exception e) {
            history.setOutput(e.getMessage());
            tenpoServiceImpl.saveHistoryAverage(history);

            throw e;
        }
    }
}