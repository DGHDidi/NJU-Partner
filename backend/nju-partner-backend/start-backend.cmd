@echo off
cd /d "%~dp0"
set "JAVA_HOME=E:\JDK17"
set "PATH=E:\JDK17\bin;E:\IntelliJ IDEA 2025.3.3\apache-maven-3.9.14\bin;%PATH%"
"E:\IntelliJ IDEA 2025.3.3\apache-maven-3.9.14\bin\mvn.cmd" spring-boot:run > backend-run.log 2> backend-run.err.log
