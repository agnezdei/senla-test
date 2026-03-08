FROM tomcat:10.1-jdk17
COPY core-module/target/hotel.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]