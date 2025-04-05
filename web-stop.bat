@echo off
chcp 65001
setlocal enabledelayedexpansion

REM -----------------------------
REM 포트 8080 사용 중인 프로세스 종료하기
REM -----------------------------
set "PID="
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    set "PID=%%a"
)

if defined PID (
    echo Port 8080 is in use by PID !PID!. Killing process...
    taskkill /PID !PID! /F
) else (
    echo No process is using port 8080.
)

pause
