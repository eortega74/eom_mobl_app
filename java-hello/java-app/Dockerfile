FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Copiar el código al contenedor
COPY . /app

# Comando por defecto: compilar y copiar el JAR
CMD ["bash", "-c", "mvn clean package -DskipTests && mkdir -p /output && cp target/*.jar /output/app.jar"]
