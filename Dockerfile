FROM eclipse-temurin:21-jre-jammy AS runtime

# Non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# JAR 파일 복사 (버전 무관, 이름 자동)
COPY ./build/libs/*.jar /app/app.jar

RUN chown -R appuser:appgroup /app
USER appuser

# JVM 기본 옵션 (운영 안정성 ↑)
ENV JAVA_OPTS="-Xms512m -Xmx1024m \
  -Dfile.encoding=UTF-8 \
  -Dsun.stdout.encoding=UTF-8 \
  -Dsun.stderr.encoding=UTF-8 \
  -Dspring.profiles.active=prod"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

EXPOSE 8080

# (선택) 컨테이너 healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1