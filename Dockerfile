FROM tomcat:10.1-jdk17

WORKDIR /usr/local/tomcat

COPY build/BackofficeReservation.war /usr/local/tomcat/backoffice.war
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]