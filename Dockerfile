FROM openjdk:17-jdk-slim

COPY target/*.jar Menu-Driven-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/Menu-Driven-0.0.1-SNAPSHOT.jar"]