# 설명
- `Web` 모듈은 웹 서버를 구축하는 모듈이다.
- `Fetcher`는 데이터를 주기적으로 가져오는 모듈이다.
- `Fetcher`는 `Web`에 의존한다.

# 실행
-  web 모듈 빌드 및 실행
```
./gradlew :web:bootJar
java -jar web/build/libs/web-0.0.1-SNAPSHOT.jar
```

- fetcher 모듈 빌드 및 실행
```
./gradlew :fetcher:bootJar
java -jar fetcher/build/libs/fetcher-0.0.1-SNAPSHOT.jar
```