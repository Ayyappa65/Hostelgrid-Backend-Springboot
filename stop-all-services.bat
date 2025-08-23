@echo off
echo Stopping all HostelGrid services...

echo Killing all Java processes (Spring Boot services)...
taskkill /f /im java.exe

echo Waiting for processes to terminate...
timeout /t 3

echo Checking for remaining Spring Boot processes...
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq java.exe" /fo csv ^| find "java.exe"') do (
    echo Found remaining Java process: %%i
    taskkill /f /pid %%i
)

echo All services stopped.
echo You can now restart services if needed.
pause