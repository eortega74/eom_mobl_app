FROM openjdk:17-jdk
WORKDIR /app

# Copia el JAR compilado desde tu proyecto local
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto (si aplica)
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
