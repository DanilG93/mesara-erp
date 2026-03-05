# FAZA 1: Maven Build (Kompajliranje koda)
# Koristimo kontejner koji u sebi ima i Javu 21 i Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Kopiramo pom.xml i skidamo zavisnosti (optimizacija da build bude brži)
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiramo ostatak izvornog koda tvog Mesara projekta
COPY src ./src

# Bildujemo projekat, pravimo .jar fajl i preskačemo testove za brži deploy
RUN mvn clean package -DskipTests

# FAZA 2: Pokretanje aplikacije (Laki kontejner)
# Ovde koristimo samo Javu, bez Mavena, da nam aplikacija zauzima manje memorije
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Kopiramo gotov .jar fajl iz FAZE 1 u ovaj novi, čisti kontejner
COPY --from=build /app/target/*.jar app.jar

# Dokumentujemo port
EXPOSE 8081

# Pokrećemo aplikaciju
ENTRYPOINT ["java", "-jar", "app.jar"]