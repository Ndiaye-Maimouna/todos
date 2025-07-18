FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /todos
COPY --from=build /app/target/*.jar todos.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENTRYPOINT ["java", "-jar", "todos.jar"]

