# Koristimo zvaničnu Javu 21 kao osnovu
FROM eclipse-temurin:21-jdk-alpine

# Pravimo folder unutar kontejnera
WORKDIR /app

# Kopiramo gotov JAR fajl iz tvog target foldera
# (Ovo zahteva da prvo uradiš Maven build)
COPY target/*.jar app.jar

# Dokumentujemo port
EXPOSE 8080

# Pokrećemo aplikaciju
ENTRYPOINT ["java", "-jar", "app.jar"]