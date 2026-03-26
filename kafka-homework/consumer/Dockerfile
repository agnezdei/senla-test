FROM amazoncorretto:17-alpine
COPY target/consumer-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]