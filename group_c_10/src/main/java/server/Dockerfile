FROM maven:latest
COPY src/main/java/server /app/src/main/java/server
COPY src/test/java/server /app/src/test/java/server
COPY pom.xml /app
WORKDIR /app
RUN ["mvn", "compile"]