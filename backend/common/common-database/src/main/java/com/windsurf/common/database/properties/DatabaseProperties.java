package com.windsurf.common.database.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据库配置属性
 */
@Data
@ConfigurationProperties(prefix = "windsurf.database")
public class DatabaseProperties {
    /**
     * 是否开启SQL性能分析
     */
    private boolean p6spy = false;

    /**
     * 是否开启多数据源
     */
    private boolean multipleDataSource = false;

    /**
     * 是否开启分页插件
     */
    private boolean enablePagination = true;

    /**
     * 是否开启乐观锁插件
     */
    private boolean enableOptimisticLocker = true;

    /**
     * 是否开启SQL打印
     */
    private boolean showSql = false;
}
