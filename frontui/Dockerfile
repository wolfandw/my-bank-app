FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY frontui/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]