# ============================================================
# 微信云托管 / 任意 Docker 环境构建文件
# 构建：docker build -t pf-private-chef .
# 运行：docker run -d -p 8080:8080 \
#         -e DB_HOST=mysql-host -e DB_NAME=pf_private_chef \
#         -e DB_USER=root -e DB_PASSWORD=xxx \
#         -e PF_TOKEN_SECRET=随机长字符串 -e WX_APPID=xxx -e WX_SECRET=xxx \
#         -e PF_NOTIFY_KEY=xxx pf-private-chef
# ============================================================

# ---- 构建阶段（阿里云镜像加速） ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY docker/settings.xml /build/settings.xml
RUN mvn -q -s /build/settings.xml dependency:go-offline
COPY src ./src
RUN mvn -q -s /build/settings.xml package -DskipTests

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Dfile.encoding=UTF-8 -Xms256m -Xmx512m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
