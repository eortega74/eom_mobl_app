Any Docker image file will work as long as you've added your self-signed certs (for duende IDP, and https DNS mapping) to your runtime stage. 

Also, ensure that your entrypoint is the following: 


WORKDIR /app
COPY --from=build /app/target/*.jar app.jar #this line copies whatever JAR file maven built into app.jar
ENTRYPOINT ["java", "-Dserver.address=0.0.0.0", "-jar", "app.jar"] 


Note: The app.jar file is expected to be available as a mounted volume in your docker-compose.yaml configuration. This is already set up by default if you’ve cloned the repository.

Please reference the following Confluence documention on how to set up MySQL with your Spring Boot project, which you will need to run:
https://confluence.xeta.rtx.com/display/NAFM/MySQL+Environment+Setup+with+Spring+Boot

How to set up self-signed certs and add them to your Docker image, also a pre-req to run:
https://confluence.xeta.rtx.com/display/NAFM/How+to+run+https+locally+in+your+Spring+Boot+project
