FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/newsIntelligent-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]