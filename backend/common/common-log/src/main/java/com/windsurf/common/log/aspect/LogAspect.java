package com.windsurf.common.log.aspect;

import com.windsurf.common.core.utils.JsonUtils;
import com.windsurf.common.log.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志记录处理
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 处理请求前执行
     */
    @Before("@annotation(logAnnotation)")
    public void doBefore(JoinPoint joinPoint, Log logAnnotation) {
        handleLog(joinPoint, logAnnotation, null, null);
    }

    /**
     * 处理完请求后执行
     */
    @AfterReturning(pointcut = "@annotation(logAnnotation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log logAnnotation, Object jsonResult) {
        handleLog(joinPoint, logAnnotation, null, jsonResult);
    }

    /**
     * 拦截异常操作
     */
    @AfterThrowing(value = "@annotation(logAnnotation)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log logAnnotation, Exception e) {
        handleLog(joinPoint, logAnnotation, e, null);
    }

    protected void handleLog(JoinPoint joinPoint, Log logAnnotation, Exception e, Object jsonResult) {
        try {
            // 获取当前的请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            
            // 获取注解信息
            String title = logAnnotation.title();
            String businessType = logAnnotation.businessType();
            
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("title", title);
            logMap.put("businessType", businessType);
            logMap.put("method", className + "." + methodName + "()");
            logMap.put("requestMethod", request.getMethod());
            logMap.put("requestUrl", request.getRequestURI());

            // 是否需要保存请求的参数
            if (logAnnotation.isSaveRequestData()) {
                // 获取方法参数
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = signature.getMethod();
                String[] parameterNames = signature.getParameterNames();
                Object[] args = joinPoint.getArgs();
                
                Map<String, Object> params = new HashMap<>();
                for (int i = 0; i < parameterNames.length; i++) {
                    params.put(parameterNames[i], args[i]);
                }
                logMap.put("requestParams", params);
            }

            // 是否需要保存响应的参数
            if (logAnnotation.isSaveResponseData() && jsonResult != null) {
                logMap.put("responseData", jsonResult);
            }

            // 处理异常信息
            if (e != null) {
                logMap.put("status", "Error");
                logMap.put("errorMessage", e.getMessage());
            } else {
                logMap.put("status", "Success");
            }

            // 记录日志
            log.info("Operation Log: {}", JsonUtils.toJsonString(logMap));
            
        } catch (Exception ex) {
            log.error("Log recording failed", ex);
        }
    }
}
