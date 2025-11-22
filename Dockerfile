# ===========================
# 1) Builder Stage
# ===========================
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /workspace

# Gradle 관련 파일 먼저 복사해서 캐시 최대 활용
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./

RUN chmod +x gradlew

# 의존성 먼저 내려받아서 이후 빌드 캐시 활용
RUN ./gradlew --no-daemon dependencies || true

# 실제 소스 복사
COPY src ./src

# 앱 JAR 빌드 (테스트는 제외)
RUN ./gradlew --no-daemon clean bootJar -x test


# ===========================
# 2) Runtime Stage
# ===========================
FROM eclipse-temurin:21-jre-jammy

# 앱 실행용 유저
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# 빌드된 JAR 가져오기 (버전 이름 상관없이 *.jar 사용)
ARG JAR_FILE=build/libs/*.jar
COPY --from=builder /workspace/${JAR_FILE} /app/app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]