FROM eclipse-temurin:17-jdk-slim
COPY target/consumer-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]