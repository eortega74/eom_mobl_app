FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

# Instala herramientas básicas para debug/red y LDAP (en Alpine)
RUN apk update && apk add --no-cache \
    openldap-clients \
    netcat-openbsd \
    curl \
    iputils \
    procps \
    binutils

WORKDIR /app

# Copia tu JAR
COPY target/ATE01-HelloJava-1.0.10.jar app.jar

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
