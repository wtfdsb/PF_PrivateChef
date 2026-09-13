# ============================================================
# 仓库根目录 Dockerfile（云托管在根目录找 Dockerfile，构建上下文=仓库根）
# 实际构建的是 PF_Private_Chef 子目录里的 Spring Boot 后端
# ============================================================

# ---- 构建阶段（阿里云镜像加速） ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY PF_Private_Chef/pom.xml .
COPY PF_Private_Chef/docker/settings.xml /build/settings.xml
RUN mvn -q -s /build/settings.xml dependency:go-offline
COPY PF_Private_Chef/src ./src
RUN mvn -q -s /build/settings.xml package -DskipTests

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Dfile.encoding=UTF-8 -Xms256m -Xmx512m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
