@echo off
chcp 65001
setlocal enabledelayedexpansion

REM -----------------------------
REM 1. 애플리케이션 빌드하기
REM -----------------------------
echo 빌드 중...
call gradlew.bat :fetcher:bootJar
if errorlevel 1 (
    echo 빌드에 실패했습니다.
    pause
    exit /b 1
)
echo 빌드 성공.

REM -----------------------------
REM 2. 포트 8081 사용 중인 프로세스 종료하기
REM -----------------------------
set "PID="
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8081 ^| findstr LISTENING') do (
    set "PID=%%a"
)

if defined PID (
    echo Port 8081 is in use by PID !PID!. Killing process...
    taskkill /PID !PID! /F
) else (
    echo No process is using port 8081.
)

REM 잠깐 대기 (프로세스 종료 시간 확보)
timeout /t 2

REM -----------------------------
REM 3. 애플리케이션 실행하기
REM -----------------------------
echo Starting application...
start "" java -Dfile.encoding=UTF-8 -jar fetcher/build/libs/fetcher-0.0.1-SNAPSHOT.jar

pause
