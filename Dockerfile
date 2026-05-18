# FAZA 1: Maven Build (Kompajliranje koda)
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
# Koristimo JRE (Java Runtime Environment) umesto JDK za maksimalnu uštedu memorije
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiramo gotov .jar fajl iz FAZE 1 u ovaj novi, čisti kontejner
COPY --from=build /app/target/*.jar app.jar

# Dokumentujemo port
EXPOSE 8080

# Pokrećemo aplikaciju
ENTRYPOINT ["java", "-jar", "app.jar"]