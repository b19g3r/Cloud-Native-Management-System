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







### 如果有 JwtAuthenticationGatewayFilter 的话， JwtAuthenticationFilter 还需要吗？



这是个很好的问题。让我解释一下这两个过滤器的区别和使用场景：

1. `JwtAuthenticationGatewayFilter`（网关层）:

- 运行在 Spring Cloud Gateway 中
- 使用响应式编程模型（基于 WebFlux）
- 主要职责是 token 的初步验证和用户信息传递
- 不会创建 Spring Security 上下文

1. `JwtAuthenticationFilter`（微服务层）:

- 运行在各个微服务中
- 基于 Servlet 模型
- 负责创建 Spring Security 上下文
- 支持 `@PreAuthorize` 等注解和细粒度的权限控制

在分布式架构中，建议的认证流程是：

```
Code
CopyInsert

客户端 -> 网关(JwtAuthenticationGatewayFilter) -> 微服务(JwtAuthenticationFilter)
```

让我修改一下之前的方案，使其更加清晰：

1. 网关层（Gateway）:

```
javaCopyInsert@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            try {
                // 只做基础的 token 格式和签名验证
                Claims claims = JwtUtils.getClaimsFromToken(jwt);
                // 将原始 token 和解析出的用户信息传递给下游服务
                ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("Authorization", token)  // 保留原始 token
                    .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                return unauthorized(exchange);
            }
        }
        
        return chain.filter(exchange);
    }
}
```

1. 微服务层：

```
javaCopyInsert@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 从请求头获取网关传递的用户信息
        String userId = request.getHeader("X-User-Id");
        String token = request.getHeader("Authorization");

        if (userId != null && token != null) {
            // 加载用户详细信息并创建 Security 上下文
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
            
            // 创建认证对象并设置到 Security 上下文中
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
```

这样的分层设计有以下优势：

1. 职责分离：
   - 网关层：负责 token 的初步验证，防止无效请求进入微服务
   - 微服务层：负责创建安全上下文，支持细粒度的权限控制
2. 性能优化：
   - 无效的 token 在网关层就被拦截，减轻了微服务的负担
   - 网关层只做必要的 token 验证，复杂的权限逻辑在微服务中处理
3. 灵活性：
   - 微服务可以根据需要选择是否启用 `JwtAuthenticationFilter`
   - 某些微服务可能不需要细粒度的权限控制，可以只依赖网关的验证
4. 安全性：
   - 双重验证机制
   - 网关层可以添加额外的安全策略（如限流、黑名单等）
   - 微服务层可以实现细粒度的权限控制

所以，我的建议是：

- 保留两个过滤器，但根据实际需求调整它们的职责
- 网关的过滤器负责基础验证和信息传递
- 微服务的过滤器负责创建安全上下文和细粒度权限控制
- 对于不需要细粒度权限控制的简单微服务，可以只使用网关的验证

这样可以在保证安全性的同时，提供足够的灵活性来满足不同微服务的需求。
