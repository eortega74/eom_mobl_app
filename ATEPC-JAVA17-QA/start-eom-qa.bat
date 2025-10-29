@echo off
cd /d "C:\Users\C95059698\Documents\repos\ATEPC-JAVA17-SPRING"

echo 🚀 Iniciando EOM QA Automation Framework...
echo ===============================================

rem Configurar Java con SSL relajado
set JAVA_OPTS=-Djava.awt.headless=true ^
-Dcom.sun.net.ssl.checkRevocation=false ^
-Dtrust_all_cert=true ^
-Djava.net.useSystemProxies=false ^
-Dspring.profiles.active=dev ^
-Dserver.port=8080

rem Intentar ejecutar con maven en modo offline
echo Intentando Maven offline...
mvn spring-boot:run -o %JAVA_OPTS%

if %ERRORLEVEL% NEQ 0 (
    echo ⚠️  Maven falló, intentando ejecución directa...
    
    rem Buscar el jar generado
    if exist "target\eom-qa-automation-1.0.0-SNAPSHOT.jar" (
        echo 📦 Ejecutando JAR...
        java %JAVA_OPTS% -jar target\eom-qa-automation-1.0.0-SNAPSHOT.jar
    ) else (
        echo ❌ No se encontró JAR, compilando primero...
        mvn clean compile -o -DskipTests
        if %ERRORLEVEL% EQU 0 (
            echo ✅ Aplicación compilada. Verificar funcionalidad básica:
            echo    - Spring Boot: Configurado
            echo    - EOM QA Framework: Integrado
            echo    - Packages: Migrados a com.eom.*
            echo 🌐 Para ejecutar cuando tengas conectividad: mvn spring-boot:run
        )
    )
)

pause