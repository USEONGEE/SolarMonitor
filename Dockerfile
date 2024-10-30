# 1. 빌드 단계: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:7.6.2-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /home/gradle/project

# Gradle Wrapper와 빌드 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x gradlew

# 소스 코드 복사
COPY src src

# 프로젝트 빌드 (테스트 제외)
RUN ./gradlew clean build -x test --no-daemon

# 2. 실행 단계: OpenJDK를 사용하여 빌드된 JAR 파일 실행
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# 포트 개방
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
