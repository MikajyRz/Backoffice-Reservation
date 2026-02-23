#!/bin/sh
set -e

# Render fournit PORT
if [ -n "$PORT" ]; then
  sed -i "s/port=\"8080\"/port=\"$PORT\"/" /usr/local/tomcat/conf/server.xml
fi

# Déploiement du WAR avec context path /BackofficeReservation
rm -rf /usr/local/tomcat/webapps/BackofficeReservation || true
rm -f /usr/local/tomcat/webapps/BackofficeReservation.war || true
mkdir -p /usr/local/tomcat/webapps/BackofficeReservation

cd /usr/local/tomcat/webapps/BackofficeReservation
jar -xf /usr/local/tomcat/backoffice.war

# Génération framework.properties à partir des variables d'env
mkdir -p /usr/local/tomcat/webapps/BackofficeReservation/WEB-INF/classes

cat > /usr/local/tomcat/webapps/BackofficeReservation/WEB-INF/classes/framework.properties <<EOF
framework.scan.package=test.java

db.url=${DB_URL}
db.user=${DB_USER}
db.password=${DB_PASSWORD}
db.driver=${DB_DRIVER}
EOF

exec catalina.sh run
