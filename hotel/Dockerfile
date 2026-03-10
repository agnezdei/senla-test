FROM maven:3.8-openjdk-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY core-module/pom.xml ./core-module/
COPY core-module/src ./core-module/src

RUN mvn dependency:go-offline -B

RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17
COPY --from=builder /build/core-module/target/hotel.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]