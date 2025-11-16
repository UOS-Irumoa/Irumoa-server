# ---- Build stage ----
FROM gradle:8.9-jdk17-alpine AS build
WORKDIR /workspace

ARG GIT_SHA=dev
LABEL org.opencontainers.image.revision=$GIT_SHA

COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle* build.gradle* ./
RUN chmod +x ./gradlew

RUN ./gradlew --no-daemon -q help || true

COPY . .

RUN ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
