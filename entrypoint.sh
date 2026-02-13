#!/usr/bin/env sh
set -e

WAR_PATH="/usr/local/tomcat/webapps/BackofficeReservation.war"
APP_DIR="/usr/local/tomcat/webapps/BackofficeReservation"

rm -rf "$APP_DIR"
mkdir -p "$APP_DIR"
cd "$APP_DIR"
jar -xf "$WAR_PATH"

PROPS_FILE="$APP_DIR/WEB-INF/classes/framework.properties"
mkdir -p "$(dirname "$PROPS_FILE")"

UPLOAD_DIR="${UPLOAD_DIR:-upload}"
SCAN_PACKAGE="${SCAN_PACKAGE:-test.java}"
DEBUG_FLAG="${FRAMEWORK_DEBUG:-false}"

DB_URL_VAL="${DB_URL:-jdbc:postgresql://localhost:5432/backoffice_reservation}"
DB_USER_VAL="${DB_USER:-postgres}"
DB_PASSWORD_VAL="${DB_PASSWORD:-postgres}"
DB_DRIVER_VAL="${DB_DRIVER:-org.postgresql.Driver}"

printf '%s\n' \
"framework.upload.dir=${UPLOAD_DIR}" \
"framework.scan.package=${SCAN_PACKAGE}" \
"framework.debug=${DEBUG_FLAG}" \
"db.url=${DB_URL_VAL}" \
"db.user=${DB_USER_VAL}" \
"db.password=${DB_PASSWORD_VAL}" \
"db.driver=${DB_DRIVER_VAL}" \
> "$PROPS_FILE"

exec catalina.sh run
