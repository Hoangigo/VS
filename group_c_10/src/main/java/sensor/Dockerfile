FROM maven:latest
COPY src/main/java/sensor /app/src/main/java/sensor
COPY src/test/java/sensor /app/src/test/java/sensor
COPY pom.xml /app
WORKDIR /app
RUN ["mvn", "compile"]