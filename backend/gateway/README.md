1. 网关服务（gateway）:

- `JwtAuthenticationGatewayFilter`: 全局认证过滤器，负责token验证
- `JwtUtils`: JWT工具类，用于解析和验证token
- 配置了路由规则和白名单





使用流程：

1. 用户通过网关访问 `/auth/login` 进行登录
2. 认证服务验证用户名密码，返回JWT token
3. 后续请求携带token访问其他接口
4. 网关验证token，并将用户信息传递给下游服务
5. token过期后，可以通过 `/auth/refresh` 刷新token