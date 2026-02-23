FROM tomcat:10.1-jdk17

WORKDIR /usr/local/tomcat

COPY build/BackofficeReservation.war /tmp/BackofficeReservation.war
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
