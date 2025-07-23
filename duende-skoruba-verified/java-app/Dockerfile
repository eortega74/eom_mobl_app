FROM openjdk:17-jdk
WORKDIR /app

# Copia el JAR compilado desde tu proyecto local
COPY target/demo-0.0.4-SNAPSHOT.jar app.jar

COPY wait-for-duende.sh wait-for-duende.sh

RUN chmod +x wait-for-duende.sh

ENTRYPOINT ["./wait-for-duende.sh"]
# Expone el puerto (si aplica)
EXPOSE 8080

# Comando para iniciar la aplicación
# ENTRYPOINT ["java", "-jar", "app.jar"]