FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY cash/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]