FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/CineHub-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]