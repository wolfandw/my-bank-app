FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY accounts/pom.xml accounts/
COPY accounts/src accounts/src/
COPY cash/pom.xml cash/
COPY cash/src cash/src/
COPY chassis/pom.xml chassis/
COPY chassis/src chassis/src/
COPY frontui/pom.xml frontui/
COPY frontui/src frontui/src/
COPY gateway/pom.xml gateway/
COPY gateway/src gateway/src/
COPY notifications/pom.xml notifications/
COPY notifications/src notifications/src/
COPY transfer/pom.xml transfer/
COPY transfer/src transfer/src/
COPY pom.xml .
RUN mvn clean package -pl :cash -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/cash/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]