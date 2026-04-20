FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY transfer/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]