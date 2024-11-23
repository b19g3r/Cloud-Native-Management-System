package com.windsurf.common.log.config;

import com.windsurf.common.log.interceptor.MDCInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 日志配置类
 */
@Configuration
public class LogConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册MDC拦截器，并应用到所有请求
        registry.addInterceptor(new MDCInterceptor()).addPathPatterns("/**");
    }
}
