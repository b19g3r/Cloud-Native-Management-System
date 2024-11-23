# Common Security Module

## 模块简介
Common Security 模块是 Windsurf 项目的安全基础设施模块，提供统一的安全认证、授权、加密等功能。该模块旨在为整个系统提供完整的安全保障机制，包括用户认证、权限控制、数据加密等核心安全功能。

## 主要功能

### 1. 认证（Authentication）
- JWT（JSON Web Token）支持
  * Token 生成和验证
  * Token 刷新机制
  * Token 黑名单管理
- OAuth2.0 集成
  * 第三方登录支持（如 GitHub、Google）
  * 授权码模式
  * 密码模式
- 多因素认证（MFA）
  * 手机验证码
  * 邮箱验证码
  * Google Authenticator

### 2. 授权（Authorization）
- RBAC（基于角色的访问控制）
  * 用户-角色-权限模型
  * 动态权限配置
  * 权限继承关系
- 资源访问控制
  * URL级别的权限控制
  * 方法级别的权限控制
  * 数据级别的权限控制

### 3. 加密服务
- 密码加密
  * BCrypt 加密
  * 密码强度校验
- 数据加密
  * AES 加密解密
  * RSA 加密解密
  * 敏感数据加密注解

### 4. 安全防护
- XSS 防护
  * 输入过滤
  * 输出编码
- CSRF 防护
  * Token 验证
  * Referer 验证
- SQL 注入防护
  * 参数校验
  * 预编译语句

### 5. 安全审计
- 安全日志记录
  * 登录日志
  * 操作日志
  * 审计日志
- 异常检测
  * 登录异常检测
  * 操作异常检测
  * 自动封禁机制

## 开发计划

### 第一阶段：基础认证授权
1. JWT 认证实现
   - Token 生成和验证
   - Token 续期机制
   - 统一认证过滤器
2. RBAC 权限模型
   - 实体类设计
   - 权限管理接口
   - 权限校验注解

### 第二阶段：OAuth2.0 和多因素认证
1. OAuth2.0 集成
   - 授权服务器配置
   - 资源服务器配置
   - 第三方登录对接
2. 多因素认证
   - 验证码生成发送
   - MFA 认证流程
   - 备用认证方案

### 第三阶段：加密服务
1. 密码服务
   - 密码加密工具
   - 密码强度校验器
2. 数据加密服务
   - 加密算法实现
   - 加密注解支持
   - 密钥管理服务

### 第四阶段：安全防护
1. XSS/CSRF 防护
   - 过滤器实现
   - 防护配置
2. SQL 注入防护
   - 参数校验器
   - 安全配置

### 第五阶段：安全审计
1. 审计日志
   - 日志记录实现
   - 日志分析工具
2. 异常检测
   - 检测规则配置
   - 自动处理机制

## 使用示例

```java
// 1. 使用权限注解
@PreAuthorize("hasRole('ADMIN')")
public void adminOperation() {
    // 管理员操作
}

// 2. 使用加密注解
@Encrypt
public void saveUserInfo(@Sensitive String idCard) {
    // 保存用户信息
}

// 3. JWT 认证
@PostMapping("/login")
public TokenDTO login(@RequestBody LoginRequest request) {
    // 登录认证
}
```

## 注意事项
1. 密钥管理：确保密钥安全存储和定期轮换
2. 性能考虑：合理使用缓存减少认证开销
3. 安全配置：根据环境配置合适的安全级别
4. 兼容性：注意与其他模块的兼容性
5. 可扩展性：预留扩展接口方便后续功能增强

## 依赖
- Spring Security
- jwt
- spring-security-oauth2
- commons-codec
- bouncy-castle
