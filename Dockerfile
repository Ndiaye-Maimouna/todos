# FROM eclipse-temurin:17-jdk-alpine
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring
# COPY target/todos.jar todos.jar
# ENTRYPOINT ["java","-jar","/todos.jar"]
#
# # Étape 1 : builder l'app avec Maven
# Étape 1 : build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : image pour exécuter le .jar
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /todos

COPY --from=build /app/target/*.jar todos.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "todos.jar"]

