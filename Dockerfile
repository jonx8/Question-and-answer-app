FROM eclipse-temurin:17-jre-alpine AS builder
WORKDIR /build
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

USER 10000:0

COPY --from=builder /build/app/dependencies/ ./
COPY --from=builder /build/app/spring-boot-loader/ ./
COPY --from=builder /build/app/snapshot-dependencies/ ./
RUN true
COPY --from=builder /build/app/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]





