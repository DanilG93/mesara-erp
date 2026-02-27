# Faza 1: Pravljenje .jar fajla pomoću Mavena (Sada koristimo Javu 21)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Faza 2: Pakovanje aplikacije za produkciju (Sada koristimo Javu 21)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]