# QAS (Quality Assurance Selenium) Framework

## 📋 Resumen del Proyecto

**QAS** es un framework de automatización de pruebas desarrollado por **Raytheon Technologies** basado en **Selenium WebDriver**, **Cucumber BDD** y **Maven**. Este framework permite ejecutar pruebas automatizadas de aplicaciones web en múltiples navegadores y entornos.

---

## 🎯 Características Principales

### ✅ **Framework Completamente Funcional**
- ✅ **Build exitoso** con Maven
- ✅ **Tests ejecutándose** correctamente
- ✅ **Navegadores soportados**: Chrome, Edge, Internet Explorer
- ✅ **Reportes automatizados** con logs detallados
- ✅ **Screenshots automáticos** en caso de fallos
- ✅ **Modo headless** para CI/CD

### 🛠️ **Tecnologías Utilizadas**

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 21.0.8 | Lenguaje base |
| **Maven** | 3.9.11 | Gestión de dependencias y builds |
| **Selenium WebDriver** | 4.20.0 | Automatización de navegadores |
| **Cucumber** | 4.2.6 | Framework BDD (Behavior Driven Development) |
| **JUnit** | 5.9.2 + 4.12 | Framework de testing |
| **WebDriverManager** | Automático | Gestión automática de drivers |
| **Log4j2** | 2.x | Sistema de logging |
| **REST Assured** | 4.3.0 | Testing de APIs |

---

## 📁 Estructura del Proyecto

```
QAS-QualityAssuranceServ/MasterProject/
├── 📁 src/
│   └── 📁 test/
│       ├── 📁 java/com/rtx/eas/qas/it/test/seleniumlite/MasterProject/
│       │   ├── 📁 common/                     # Configuración común
│       │   │   └── TestRunner.java            # Ejecutor principal Cucumber
│       │   ├── 📁 cucumber/step/              # Definiciones de pasos
│       │   │   ├── 📁 common/
│       │   │   │   └── CommonSteps.java       # Pasos genéricos (lanzar navegador)
│       │   │   └── 📁 AppSteps/
│       │   │       └── HomePageSteps.java     # Pasos específicos (Google, etc.)
│       │   ├── 📁 selenium/                   # Core del framework Selenium
│       │   │   ├── BaseConfiguration.java    # Configuración base y logging
│       │   │   ├── Browsers.java             # Gestión de navegadores
│       │   │   ├── NavigationBoundPage.java  # Navegación de páginas
│       │   │   └── 📁 Pages/                  # Page Object Model
│       │   │       └── SeleniumPageObjectExample.java
│       │   └── 📁 util/                       # Utilidades
│       │       └── OneClickEncryptor.java     # Encriptación de credenciales
│       └── 📁 resources/
│           ├── 📁 features/                   # Archivos Gherkin (.feature)
│           │   └── GoogleExample.feature     # Ejemplo de test BDD
│           ├── 📁 selenium/driverServer/      # Drivers de navegadores
│           ├── test.properties               # Configuración principal
│           ├── database.properties           # Configuración de BD
│           ├── api.properties               # Configuración de APIs
│           ├── sso.properties               # Configuración SSO
│           ├── users.properties             # Usuarios de prueba
│           └── log4j2.properties            # Configuración de logs
├── 📁 target/                               # Archivos generados por Maven
├── 📁 results/                              # Reportes y screenshots
├── pom.xml                                  # Configuración Maven
├── settings-build.xml                       # Settings Maven personalizados
└── README.md                               # Esta documentación
```

---

## 🏗️ Arquitectura del Framework

### 🎭 **Patrón Page Object Model (POM)**
```java
SeleniumPageObjectExample extends NavigationBoundPage {
    @FindBy(name="q") WebElement searchField;
    @FindBy(name="btnI") WebElement luckyButton;
    
    public void searchFor(String text) {
        searchField.sendKeys(text);
    }
}
```

### 🥒 **Behavior Driven Development (BDD) con Cucumber**
```gherkin
Feature: Example
  @Example1
  Scenario Outline: Example Scenario 1
    Given I launch a <browser> browser for <test name>
    And Google is opened
    Then the <page name> is displayed
    
    Examples:
      | browser | test name           | page name |
      | "Chrome"| "Log4j2 Test"      | "Google"  |
```

### 🔧 **Gestión Automática de Drivers**
- **WebDriverManager** descarga automáticamente drivers compatibles
- **Sin configuración manual** de rutas de drivers
- **Soporte multi-navegador** automático

---

## 🚀 Configuración y Ejecución

### 📋 **Prerequisitos**
- ✅ **Java 21** o superior
- ✅ **Maven 3.9+** 
- ✅ **Google Chrome** instalado
- ✅ **Git** para clonar el repositorio

### ⚙️ **Configuración Inicial**

1. **Clonar el repositorio:**
```bash
git clone <repository-url>
cd QAS-QualityAssuranceServ/MasterProject
```

2. **Configurar Maven settings (si es necesario):**
```bash
# Para entornos corporativos sin proxy
cp settings-build.xml ~/.m2/settings.xml
```

3. **Configurar URLs de prueba en `test.properties`:**
```properties
# URL principal para pruebas
test.url=https://www.google.com

# Configuración de proxy (vacío para local)
chrome.network.proxy=
edge.network.proxy=
```

### 🏃‍♂️ **Ejecutar Pruebas**

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar con settings personalizados
mvn test -s settings-build.xml

# Solo compilar
mvn compile

# Build completo
mvn clean install
```

---

## 🎯 Casos de Uso y Ejemplos

### 📝 **Ejemplo de Test Básico**
```java
@Given("I launch a {string} browser for {string}")
public void i_launch_a_browser_for(String browser, String testName) {
    browsers.startTest(browser, testName);
}

@Given("Google is opened")
public void google_is_opened() {
    pageExample = new SeleniumPageObjectExample();
    assertTrue("FAIL - Unable to open Google", pageExample.openPage());
}

@Then("the {string} is displayed")
public void the_is_displayed(String expectedTitle) {
    String actualTitle = pageExample.getPageTitle();
    assertTrue("Page title verification failed", actualTitle.contains(expectedTitle));
}
```

### 🌐 **Configuración Multi-Browser**
```java
// Chrome (por defecto)
browsers.startTest("Chrome", "Test Name");

// Microsoft Edge
browsers.startTest("Edge", "Test Name");

// Internet Explorer
browsers.startTest("IE", "Test Name");
```

### 📊 **Modos de Ejecución**
```java
// Modo local (en tu máquina)
test.executionLocation=local

// Modo remoto (Selenium Grid)
test.executionLocation=hRTN  // o hUTC
```

---

## 📈 Reportes y Logging

### 📋 **Sistema de Logging**
- **Log4j2** configurado para logging detallado
- **Logs por test** en directorios separados
- **Niveles**: INFO, DEBUG, ERROR, WARN

### 📸 **Screenshots Automáticos**
```java
// Screenshots automáticos en fallos
outputDirName: results\Log4j2_Test_Headless\Chrome\2025_10_23_16_00_18\
```

### 📊 **Estructura de Reportes**
```
results/
├── TestName/
│   ├── Browser/
│   │   ├── Timestamp/
│   │   │   ├── screenshots/
│   │   │   └── logs/
```

---

## 🔧 Solución de Problemas

### ❌ **Problemas Comunes y Soluciones**

#### 1. **Error de Proxy/Conectividad**
```bash
# Problema: WebDriverManager no puede descargar drivers
# Solución: Configurar proxy vacío en test.properties
chrome.network.proxy=
edge.network.proxy=
```

#### 2. **Chrome no encontrado**
```bash
# Problema: "cannot find Chrome binary"
# Solución: Instalar Chrome o usar modo headless
options.addArguments("--headless");
```

#### 3. **Error de compilación Maven**
```bash
# Problema: Dependencias no descargadas
# Solución: 
mvn clean compile -U  # Forzar actualización
mvn dependency:resolve  # Verificar dependencias
```

#### 4. **Tests fallan con "Page not found"**
```bash
# Problema: URL incorrecta en test.properties
# Solución: Verificar y cambiar URL
test.url=https://www.google.com  # Para pruebas básicas
```

---

## 🔐 Configuración Corporativa

### 🏢 **Entorno Raytheon/RTX**
```properties
# URLs corporativas (requieren VPN)
auth.url=https://webauth.ext.ray.com/...
test.url=https://wd5-impl.workday.com/...

# Proxies corporativos
chrome.network.proxy=bos-proxy.ext.ray.com:80
edge.network.proxy=bos-proxy.ext.ray.com:80

# Execution locations
utcHub.IP=http://10.165.8.184:80/wd/hub
rtnHub.IP=http://10.165.8.184:80/wd/hub
```

### 🔒 **Gestión de Credenciales**
- **OneClickEncryptor** para credenciales seguras
- **users.properties** para usuarios de prueba
- **SSO integration** disponible

---

## 📋 Historial de Cambios

### 🎉 **Sesión Actual (Octubre 23, 2025)**
- ✅ **Framework completamente configurado y operativo**
- ✅ **Proxy issues resueltos** para ambiente local
- ✅ **Chrome driver funcionando** automáticamente
- ✅ **Test exitoso** ejecutándose end-to-end
- ✅ **Documentación completa** creada
- ✅ **Build pipeline** funcionando con Maven

### 🔧 **Modificaciones Realizadas**
1. **Proxy configuration** deshabilitado para testing local
2. **Chrome installation** y configuración automática
3. **URL configuration** cambiada a Google para pruebas básicas
4. **Headless mode** configurado para CI/CD
5. **Maven settings** personalizados sin proxy corporativo

---

## 🤝 Contribución y Mantenimiento

### 👨‍💻 **Equipo de Desarrollo**
- **Raytheon EAS-QAS Team**
- **Framework Owner**: nrp0236009/40003339

### 📞 **Contacto y Soporte**
- **Contact Methods**: EAS-QAS
- **Documentation**: Este README.md
- **Issue Tracking**: A través del repositorio Git

### 🔄 **Proceso de Actualización**
1. Hacer cambios en feature branches
2. Ejecutar tests: `mvn test`
3. Verificar build: `mvn clean install`
4. Commit y push cambios
5. Crear Pull Request

---

## 📚 Referencias y Recursos

### 🔗 **Enlaces Útiles**
- [Selenium WebDriver Documentation](https://selenium.dev/documentation/)
- [Cucumber BDD Framework](https://cucumber.io/docs)
- [Maven Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
- [Page Object Model Pattern](https://selenium.dev/documentation/test_practices/encouraged/page_object_models/)

### 📖 **Documentación Adicional**
- `src/test/resources/` - Archivos de configuración
- `pom.xml` - Dependencias y plugins Maven
- Feature files - Casos de prueba en formato Gherkin

---

## 🎯 Estado Actual

### ✅ **Completamente Operativo**
```
✅ Build Success
✅ Tests Passing  
✅ Framework Ready
✅ Documentation Complete
```

**El framework QAS está listo para desarrollo y ejecución de pruebas automatizadas.** 🚀

---

*Documentación creada el 23 de octubre de 2025*  
*Framework Version: MasterProject 0.0.1-SNAPSHOT*