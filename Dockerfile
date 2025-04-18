# Stage 1: build
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/safeville-1.0.0.jar ./safeville.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/safeville.jar"]
