REM Original Author - Kiran Shirali (kshirali@ebay.com)
SET PROJECT_DIR=%~dp0
call cd /D %PROJECT_DIR%
call mvn  clean eclipse:clean
call mvn  eclipse:eclipse
call mvn   -e clean install
