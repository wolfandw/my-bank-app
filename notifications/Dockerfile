FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY notifications/target/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]