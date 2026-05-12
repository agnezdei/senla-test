FROM maven:3.8-openjdk-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17
COPY --from=builder /build/target/library.war /usr/local/tomcat/webapps/ROOT.war
COPY database.docker.properties /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/database.properties
EXPOSE 8080
CMD ["catalina.sh", "run"]