# Common Database Module

## 模块简介
Common Database 模块是 Windsurf 项目的数据库基础设施模块，提供统一的数据库访问、ORM配置、连接池管理等功能。该模块旨在为整个系统提供统一的数据访问层，包括数据源配置、事务管理、数据库版本控制等核心功能。

## 主要功能

### 1. 数据源管理
- 多数据源支持
  * 主从数据源配置
  * 动态数据源切换
  * 数据源健康检查
- 连接池配置
  * Hikari连接池
  * 连接池监控
  * 性能优化配置

### 2. ORM框架集成
- MyBatis-Plus配置
  * 通用CRUD接口
  * 分页插件
  * 乐观锁插件
- JPA支持
  * 实体映射
  * 审计功能
  * 查询优化

### 3. 数据库版本控制
- Flyway集成
  * 版本迁移脚本
  * 基线版本管理
  * 多环境支持
- 数据初始化
  * 基础数据导入
  * 测试数据管理

### 4. 事务管理
- 声明式事务
  * 事务注解支持
  * 事务传播机制
  * 事务隔离级别
- 分布式事务
  * Seata集成
  * 最终一致性支持

### 5. 数据库工具
- SQL监控
  * 慢查询日志
  * 执行计划分析
- 数据库文档
  * 表结构文档
  * 字段说明维护

## 开发计划

### 第一阶段：基础配置
1. 数据源配置
   - Hikari连接池配置
   - 数据源属性配置
   - 健康检查实现
2. MyBatis-Plus集成
   - 基础配置
   - 通用Mapper
   - 代码生成器

### 第二阶段：多数据源支持
1. 动态数据源
   - 数据源切换实现
   - 读写分离支持
2. 分库分表方案
   - Sharding-JDBC集成
   - 分片策略配置

### 第三阶段：数据库版本控制
1. Flyway集成
   - 迁移脚本管理
   - 版本控制流程
2. 数据初始化
   - 初始化脚本
   - 环境配置

### 第四阶段：性能优化
1. 查询优化
   - 缓存整合
   - 分页优化
2. 监控告警
   - 性能指标采集
   - 告警规则配置

## 使用示例

```java
// 1. 使用通用Mapper
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 自定义查询方法
}

// 2. 多数据源切换
@DS("slave")
public List<User> queryFromSlave() {
    return userMapper.selectList(null);
}

// 3. 分页查询
public IPage<User> queryByPage(Page<User> page) {
    return userMapper.selectPage(page, null);
}
```

## 注意事项
1. 数据库连接：合理配置连接池参数
2. 性能优化：注意SQL语句优化和索引使用
3. 版本管理：规范数据库版本控制流程
4. 数据安全：注意敏感数据处理和备份策略
5. 扩展性：预留分库分表接口

## 依赖
- MySQL Connector
- MyBatis-Plus
- Hikari CP
- Flyway
- Seata (可选)
- Sharding-JDBC (可选)
- p6spy (开发环境)
