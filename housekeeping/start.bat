@echo off
echo 启动家政服务管理系统...
cd /d "%~dp0"
echo 当前目录: %CD%
echo 正在启动服务，请稍等...
java -jar target\housekeeping-0.0.1-SNAPSHOT.jar
pause
