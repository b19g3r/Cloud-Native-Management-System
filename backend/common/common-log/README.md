# Common Log Module

## 简介
Common Log 模块是一个通用的日志处理模块，提供了完整的日志记录、查询、导出和管理功能。该模块支持操作日志的自动记录、异步处理、敏感信息过滤等特性。

## 已实现功能

### 1. 基础日志配置
- [x] Logback 配置文件
- [x] 日志输出格式定义
- [x] 日志文件轮转策略

### 2. 日志工具类
- [x] LogUtils 工具类
- [x] 支持多种日志级别（INFO、DEBUG、ERROR）
- [x] 异常信息记录

### 3. 操作日志注解
- [x] @Log 注解定义
- [x] 支持模块和业务类型配置
- [x] 请求和响应参数记录控制

### 4. 日志切面
- [x] AOP 自动日志记录
- [x] 方法调用信息获取
- [x] 异常处理

## 待实现功能

### 1. 操作日志持久化
- [ ] 操作日志实体类（OperationLog）
  - 基础字段定义
  - 数据库表设计
- [ ] 日志存储服务（OperationLogService）
  - 日志保存接口
  - 日志查询功能

### 2. 日志追踪
- [ ] MDC 工具类
  - TraceId 生成和管理
  - 用户信息关联
  - 上下文信息清理

### 3. 敏感信息处理
- [ ] 敏感数据过滤器
  - 敏感字段定义
  - 数据脱敏规则
  - 过滤实现

### 4. 异步处理
- [ ] 异步日志配置
  - 线程池配置
  - 异步处理实现
  - 性能优化

### 5. 日志管理接口
- [ ] REST API 接口
  - 日志查询接口
  - 日志删除接口
  - 分页查询支持

### 6. 日志导出功能
- [ ] 导出服务
  - 多种格式支持（CSV、Excel）
  - 大数据量处理
  - 导出进度跟踪

### 7. 日志清理
- [ ] 自动清理任务
  - 定时任务配置
  - 清理策略定义
  - 清理实现

## 使用示例

### 1. 添加依赖
```xml
<dependency>
    <groupId>com.windsurf</groupId>
    <artifactId>common-log</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 2. 使用日志注解
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Log(title = "用户管理", businessType = "创建用户")
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        // 业务逻辑
        return Result.success(user);
    }
}
```

### 3. 使用日志工具类
```java
public class ServiceImpl {
    private static final Logger logger = LogUtils.getLogger(ServiceImpl.class);
    
    public void doSomething() {
        LogUtils.info(logger, "Processing started");
        try {
            // 业务逻辑
        } catch (Exception e) {
            LogUtils.error(logger, "Processing failed", e);
        }
    }
}
```

## 注意事项
1. 敏感信息处理：确保敏感信息不被记录到日志中
2. 性能考虑：使用异步处理避免日志记录影响主业务流程
3. 存储空间：配置合理的日志清理策略
4. 安全性：注意日志访问权限控制





1. **操作日志持久化**：
   - 创建OperationLog实体类
   - 实现日志存储服务
   - 设计数据库表结构
2. **敏感信息处理**：
   - 实现敏感数据过滤器
   - 添加数据脱敏注解
   - 提供脱敏工具类
3. **异步日志处理**：
   - 实现异步日志队列
   - 添加异步日志处理器
   - 配置异步线程池
4. **日志管理接口**：
   - 日志查询接口
   - 日志导出功能
   - 日志清理接口
5. **完善现有功能**：
   - 为LogAspect添加更多的日志属性（如IP地址、浏览器信息等）
   - 添加日志级别动态调整功能
   - 增加日志监控和告警功能

我建议按照以下顺序实现：

1. 先完成操作日志持久化，这是基础功能
2. 然后实现敏感信息处理，这关系到数据安全
3. 接着做异步日志处理，提升性能
4. 最后实现日志管理接口
