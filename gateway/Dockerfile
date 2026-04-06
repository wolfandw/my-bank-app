FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY gateway/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]