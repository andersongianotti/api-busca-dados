# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=secret,id=maven_settings,required=false \
    if [ -f /run/secrets/maven_settings ]; then mkdir -p /root/.m2 && cp /run/secrets/maven_settings /root/.m2/settings.xml; fi && \
    mvn dependency:go-offline -q
COPY src ./src
RUN --mount=type=secret,id=maven_settings,required=false \
    if [ -f /run/secrets/maven_settings ]; then mkdir -p /root/.m2 && cp /run/secrets/maven_settings /root/.m2/settings.xml; fi && \
    mvn package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER 1000
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
