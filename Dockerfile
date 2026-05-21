# STAGE 1: Build
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
# Ensure the maven wrapper is executable
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# STAGE 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# This looks for any jar in the target folder and copies it
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
