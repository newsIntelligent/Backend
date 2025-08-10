# Build Stage
FROM gradle:8.5-jdk17 AS builder

ENV TZ=Asia/Seoul

WORKDIR /app

# Gradle 빌드에 필요한 전체 프로젝트 복사
COPY . .

# 빌드 실행 (테스트 생략)
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# Run Stage
FROM eclipse-temurin:17-jdk

ENV TZ=Asia/Seoul

WORKDIR /app

# 빌드 결과만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]