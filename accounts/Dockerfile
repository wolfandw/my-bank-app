FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY accounts/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]