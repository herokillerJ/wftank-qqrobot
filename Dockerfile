# syntax=docker/dockerfile:experimental
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app
# maven files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
# project files
COPY robot-app/ robot-app/
COPY robot-common/ robot-common/
COPY robot-dao/ robot-dao/
COPY robot-discord/ robot-discord/
COPY robot-schedule/ robot-schedule/
COPY robot-search-engine/ robot-search-engine/

# build
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean install -s .mvn/wrapper/settings.xml -Dmaven.test.skip=true
# spring boot layer
#RUN mkdir -p dependency && (cd dependency; jar -xf ../robot-app/target/*.jar)
RUN mkdir -p extracted && java -Djarmode=layertools -jar robot-app/target/*.jar extract --destination extracted

FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
WORKDIR /workspace/app
ARG EXTRACTED=/workspace/app/extracted
COPY --from=build  ${EXTRACTED}/dependencies/ ./
COPY --from=build  ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build  ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build  ${EXTRACTED}/application/ ./
# 复制必要配置文件
CMD ["java","-Dmirai.slider.captcha.supported","-Dxyz.cssxsh.mirai.tool.KFCFactory.config=/workspace/app/content-dir/KFCFactory.json","org.springframework.boot.loader.JarLauncher"]