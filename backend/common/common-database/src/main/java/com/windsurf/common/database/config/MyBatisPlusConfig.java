package com.windsurf.common.database.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.windsurf.common.database.properties.DatabaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus配置
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(DatabaseProperties.class)
public class MyBatisPlusConfig {

    private final DatabaseProperties properties;

    public MyBatisPlusConfig(DatabaseProperties properties) {
        this.properties = properties;
    }

    /**
     * 配置MybatisPlus插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加分页插件
        if (properties.isEnablePagination()) {
            PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
            // 设置最大单页限制数量，默认500条，-1不受限制
            paginationInterceptor.setMaxLimit(500L);
            interceptor.addInnerInterceptor(paginationInterceptor);
        }

        // 添加乐观锁插件
        if (properties.isEnableOptimisticLocker()) {
            interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        }

        return interceptor;
    }
}
