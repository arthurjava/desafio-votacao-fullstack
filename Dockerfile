FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

# Runtime stage - use JRE to keep image small
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/desafio-votacao-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]