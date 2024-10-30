# 1. 빌드 단계: Maven을 사용하여 애플리케이션 빌드
FROM maven:3.8.6-openjdk-17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트의 pom.xml과 소스 코드를 복사
COPY pom.xml .
COPY src ./src

# 의존성 다운로드 및 애플리케이션 빌드
RUN mvn clean package -DskipTests

# 2. 실행 단계: OpenJDK를 사용하여 빌드된 JAR 파일 실행
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
COPY --from=build /app/target/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
