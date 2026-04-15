# Build stage
# Build context must be ./back (set in docker-compose.yml)
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Install parent POM to local Maven repository
COPY financial-app-parent/pom.xml financial-app-parent/pom.xml
RUN mvn -f financial-app-parent/pom.xml install -N -q

# Resolve dependencies (cached layer — only re-runs when pom.xml changes)
COPY ms-notifications/pom.xml ms-notifications/pom.xml
RUN mvn -f ms-notifications/pom.xml dependency:resolve -q

# Build
COPY ms-notifications/src ms-notifications/src
RUN mvn -f ms-notifications/pom.xml clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/ms-notifications/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
