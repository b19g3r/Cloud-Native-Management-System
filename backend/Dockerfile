# 使用多阶段构建
# 构建阶段
FROM maven:3.8-openjdk-17 AS builder

# 设置工作目录
WORKDIR /build

# 复制Maven配置文件
COPY pom.xml .
COPY common/pom.xml common/
COPY common/common-core/pom.xml common/common-core/
COPY common/common-redis/pom.xml common/common-redis/

# 下载依赖（利用Docker缓存机制）
RUN mvn dependency:go-offline

# 复制源代码
COPY . .

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:17-slim

# 设置工作目录
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai

# 复制构建产物
COPY --from=builder /build/*/target/*.jar ./

# 暴露端口（具体端口在运行时通过环境变量指定）
EXPOSE 8080

# 启动命令（具体jar包名在运行时通过环境变量指定）
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ${SERVICE_NAME}.jar ${ARGS}"]
