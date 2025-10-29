@echo off
echo =========================================
echo    PRUEBA LOCAL EOM-QA AUTOMATION
echo =========================================
echo.

cd /d "C:\Users\C95059698\Documents\repos\ATEPC-JAVA17-SPRING"

echo [INFO] Verificando estructura del proyecto...
if exist "target\classes\com\eom\demo\EomQaApplication.class" (
    echo [OK] Clase principal encontrada: EomQaApplication
) else (
    echo [ERROR] Clase principal no encontrada
    goto :error
)

echo.
echo [INFO] Verificando dependencias Maven compiladas...
if exist "target\classes" (
    echo [OK] Clases compiladas encontradas
) else (
    echo [ERROR] No se encontraron clases compiladas
    goto :error
)

echo.
echo [INFO] Listando endpoints disponibles del framework EOM-QA:
echo   - GET /                    : Página principal
echo   - GET /status              : Estado del sistema
echo   - GET /qa                  : Panel de QA
echo   - GET /api/qa/status       : API - Estado del framework
echo   - GET /api/qa/components   : API - Componentes del framework

echo.
echo [INFO] Ejecutando prueba de compilación offline...
mvn clean compile -o -q

if %ERRORLEVEL% EQU 0 (
    echo [OK] Compilación exitosa
) else (
    echo [ERROR] Falló la compilación
    goto :error
)

echo.
echo [INFO] Verificando archivos de configuración...
if exist "src\main\resources\application.properties" (
    echo [OK] Configuración de aplicación encontrada
) else (
    echo [WARN] Archivo de configuración no encontrado
)

echo.
echo [INFO] Listando clases compiladas del framework EOM-QA:
dir /b target\classes\com\eom\demo\*.class 2>nul
dir /b target\classes\com\eom\qa\demo\*.class 2>nul

echo.
echo =========================================
echo [SUCCESS] PRUEBA LOCAL COMPLETADA
echo =========================================
echo.
echo El framework EOM-QA está listo para uso.
echo Para ejecutar la aplicación completa, use:
echo   mvn spring-boot:run
echo.
echo O acceda a los endpoints REST directamente
echo una vez que la aplicación esté ejecutándose.
echo.
goto :end

:error
echo.
echo =========================================
echo [ERROR] PRUEBA FALLÓ
echo =========================================
echo Verifique la configuración del proyecto.
echo.

:end
pause