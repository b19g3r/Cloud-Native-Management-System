package com.windsurf.common.log.utils;

import org.slf4j.MDC;
import java.util.UUID;

/**
 * MDC工具类，用于在日志中添加追踪信息
 */
public class MDCUtils {
    /**
     * traceId 常量
     */
    public static final String TRACE_ID = "traceId";
    
    /**
     * userId 常量
     */
    public static final String USER_ID = "userId";

    /**
     * 生成并设置traceId
     */
    public static String setTraceId() {
        String traceId = generateTraceId();
        MDC.put(TRACE_ID, traceId);
        return traceId;
    }

    /**
     * 获取当前traceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 设置用户ID
     */
    public static void setUserId(String userId) {
        if (userId != null) {
            MDC.put(USER_ID, userId);
        }
    }

    /**
     * 获取用户ID
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }

    /**
     * 清理MDC中的数据
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * 生成traceId
     */
    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
