# common-core 模块说明
## 结果相关：
- result/R.java: 通用响应结构
- result/IResultCode.java: 响应码接口
- result/ResultCode.java: 响应码枚举
## 异常相关：
- exception/BusinessException.java: 业务异常类
- exception/GlobalExceptionHandler.java: 全局异常处理器
## 实体相关：
- entity/BaseEntity.java: 基础实体类
## 工具类：
- utils/SecurityUtils.java: 安全工具类
- utils/WebUtils.java: Web工具类
- utils/ThreadPoolUtils.java: 线程池工具类
## 分页相关：
- page/PageQuery.java: 分页查询参数
- page/PageResult.java: 分页结果封装



### todo
常用工具类的补充：
StringUtils：字符串处理工具
DateUtils：日期时间工具
ValidateUtils：通用验证工具
EncryptUtils：加密解密工具
JsonUtils：JSON处理工具（基于Jackson）
通用注解的开发：
@DataScope：数据权限注解
@Log：操作日志注解
@RateLimit：接口限流注解
@RepeatSubmit：防重复提交注解
增强异常处理：
添加更多业务异常类型
完善异常日志记录
添加异常链路追踪


EncryptUtils - 加密工具类：
提供常用的加密算法实现：MD5、SHA256、SHA512
支持对称加密(AES)和非对称加密(RSA)
支持Base64编码/解码
提供密钥生成功能
支持加盐加密
JsonUtils - JSON工具类：
基于Jackson实现JSON序列化和反序列化
支持对象与JSON字符串的互相转换
支持对象与Map的互相转换
支持JSON数组的解析
提供格式化JSON输出
支持深拷贝对象
配置了常用的Jackson特性
这两个工具类都遵循了以下设计原则：

提供静态方法以便于调用
使用Lombok简化代码
添加了全面的异常处理和日志记录
提供了详细的JavaDoc文档
保持与现有工具类风格一致