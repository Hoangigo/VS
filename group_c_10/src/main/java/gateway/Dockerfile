FROM maven:latest
COPY src/main/java/gateway /app/src/main/java/gateway
COPY src/test/java/gateway /app/src/test/java/gateway
COPY pom.xml /app
WORKDIR /app
RUN ["mvn", "compile"]