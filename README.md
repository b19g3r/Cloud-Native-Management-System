# Cloud Native Management System

## 项目架构
基于Spring Cloud Alibaba的微服务架构管理后台系统。

### 技术栈
#### 后端技术栈
- Spring Boot 2.7.x
- Spring Cloud Alibaba 2021.0.5.0
- Spring Cloud Gateway
- Nacos 2.2.0 (服务注册与配置中心)
- RocketMQ 4.9.4 (消息队列)
- Sentinel (限流熔断)
- OpenFeign (服务调用)
- MySQL 8.0
- Redis 6.x
- Elasticsearch 7.17.x (日志存储)
- JWT + SSO (认证授权)
- MyBatis-Plus (ORM框架)

#### 前端技术栈
- Vue 3
- Vite
- Pinia (状态管理)
- Vue Router 4
- Element Plus
- Axios
- ECharts (图表展示)

#### 监控和运维
- Prometheus + Grafana (监控)
- ELK Stack (日志收集)
- SkyWalking (链路追踪)
- Docker + Kubernetes (容器化和编排)

### 项目结构
```
├── backend/
│   ├── common/                    # 公共模块
│   │   ├── common-core/          # 核心工具
│   │   ├── common-redis/         # Redis工具
│   │   ├── common-log/           # 日志模块
│   │   └── common-security/      # 安全工具
│   ├── gateway/                  # 网关服务
│   ├── auth/                     # 认证服务
│   ├── system/                   # 系统服务
│   │   ├── system-api/          # 系统服务API
│   │   └── system-service/      # 系统服务实现
│   └── monitor/                  # 监控服务
│
├── frontend/
│   ├── src/
│   │   ├── api/                 # API接口
│   │   ├── components/          # 公共组件
│   │   ├── views/              # 页面
│   │   ├── store/              # 状态管理
│   │   └── router/             # 路由配置
│   └── package.json
│
├── deploy/                      # 部署相关配置
│   ├── docker/                 # Docker配置
│   └── k8s/                    # Kubernetes配置
│
└── docs/                       # 项目文档
```

### 核心功能模块
1. 用户认证与授权
2. 系统管理
3. 日志管理
4. 监控告警
5. 配置管理

### 系统要求
- JDK 17
- Node.js 16+
- Maven 3.8+
- Docker
- Kubernetes

### 快速开始
1. 启动基础服务
```bash
cd deploy/docker
docker-compose up -d nacos mysql redis elasticsearch
```

2. 启动后端服务
```bash
cd backend
mvn clean package
```

3. 启动前端服务
```bash
cd frontend
npm install
npm run dev
```

### 性能特性
- 支持2000W用户规模
- 日访问量3000W
- 服务高可用
- 分布式事务支持
- 分布式缓存
- 消息队列解耦

### 监控指标
- 系统性能监控
- 业务监控
- 链路追踪
- 日志聚合
- 告警通知
