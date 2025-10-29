FROM openjdk:17-jdk-slim

# Install system dependencies required for Selenium and Chrome integration
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    gnupg \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Configure application working directory
WORKDIR /app

# Create reports directory for Selenium test output
RUN mkdir -p /app/reports

# Copy Spring Boot application JAR artifact
COPY target/*.jar /app/eom-qa-app.jar

# Copy Maven dependencies cache for faster builds (optional)
COPY m2-cache/ /root/.m2/

# Expose Spring Boot application port
EXPOSE 8080

# Configure application health check endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set environment variables for Selenium Grid integration
ENV SELENIUM_HOST=selenium
ENV SPRING_PROFILES_ACTIVE=qa

# Launch Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/eom-qa-app.jar"]