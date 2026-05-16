@echo off
if "%1"=="" goto start
if "%1"=="start" goto start
if "%1"=="db" goto db
if "%1"=="app" goto app
if "%1"=="stop" goto stop
if "%1"=="restart" goto restart
if "%1"=="clean" goto clean
echo Unknown command: %1
echo Usage: run.bat [start^|db^|app^|stop^|restart^|clean]
exit /b 1

:restart
docker compose down -v
docker compose up -d
exit /b 0

:start
:db
echo Starting database...
docker compose up -d
echo Waiting for MySQL to be ready...
:wait
docker exec travel-reservation-db mysqladmin ping -uroot -ppassword123 --silent 2>nul
if errorlevel 1 (
    timeout /t 1 /nobreak >nul
    goto wait
)
echo Database is ready.
if "%1"=="db" exit /b 0

:app
echo Building and running Java app...
mvnw -q compile exec:java -Dexec.mainClass="org.group40.ProjectFrame"
exit /b 0

:stop
docker compose down
exit /b 0

:clean
mvnw -q clean
exit /b 0