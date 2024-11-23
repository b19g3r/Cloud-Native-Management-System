package com.windsurf.common.log.interceptor;

import com.windsurf.common.log.utils.MDCUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MDC拦截器，用于在请求开始时设置traceId，请求结束时清理MDC
 */
public class MDCInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 生成新的traceId
        MDCUtils.setTraceId();
        
        // 从请求头中获取用户ID（如果有的话）
        String userId = request.getHeader("X-User-Id");
        if (userId != null) {
            MDCUtils.setUserId(userId);
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理MDC数据
        MDCUtils.clear();
    }
}
