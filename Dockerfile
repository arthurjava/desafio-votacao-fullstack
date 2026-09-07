FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

RUN apk add --no-cache maven
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl
COPY --from=build /app/target/desafio-votacao-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]