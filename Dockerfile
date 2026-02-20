FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /opt/app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]