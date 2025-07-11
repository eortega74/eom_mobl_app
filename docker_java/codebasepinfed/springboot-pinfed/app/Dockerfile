FROM openjdk:17-slim

# Instala herramientas básicas de red y LDAP
RUN apt-get update && apt-get install -y \
    ldap-utils \
    netcat \
    curl \
    iputils-ping \
    procps \
    && rm -rf /var/lib/apt/lists/*
RUN apt update && apt install -y binutils

WORKDIR /app

# Copia tu JAR
COPY target/ATE01-HelloJava-1.0.10.jar app.jar

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]


