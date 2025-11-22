# ============================
# Stage 1: Build with Gradle
# ============================
FROM gradle:8.6-jdk21 AS builder

WORKDIR /workspace

COPY . .

# Gradle 캐시 적극 활용
RUN ./gradlew clean build -x test --parallel --build-cache

# ============================
# Stage 2: Runtime (최종 이미지)
# ============================
FROM eclipse-temurin:21-jre-jammy

# 보안상 non-root
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /workspace/build/libs/*.jar /app/app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]