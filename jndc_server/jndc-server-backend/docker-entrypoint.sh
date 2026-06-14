#!/bin/sh
set -eu

APP_HOME=/opt/jndc
RUNTIME_HOME="${JNDC_RUNTIME_HOME:-/opt/jndc/runtime-home}"
WORKSPACE="${RUNTIME_HOME}/.jndc/server"
CONFIG_PATH="${JNDC_SERVER_CONFIG:-${WORKSPACE}/conf/config.yml}"
CONFIG_DIR="$(dirname "${CONFIG_PATH}")"
LOG_DIR="${WORKSPACE}/logs"
CLASSPATH="${APP_HOME}/conf:${APP_HOME}/lib/*"
JNDC_SERVER_SECRET="${JNDC_SERVER_SECRET:-change-this-secret}"
JNDC_ADMIN_USERNAME="${JNDC_ADMIN_USERNAME:-admin}"
JNDC_ADMIN_PASSWORD="${JNDC_ADMIN_PASSWORD:-admin123456}"

escape_sed_replacement() {
  printf '%s' "$1" | sed 's/[\/&]/\\&/g'
}

mkdir -p "${CONFIG_DIR}" "${LOG_DIR}"

if [ ! -f "${CONFIG_PATH}" ]; then
  cp "${APP_HOME}/conf/config.template.yml" "${CONFIG_PATH}"
  sed -i 's/bindIp: "127.0.0.1"/bindIp: "0.0.0.0"/' "${CONFIG_PATH}"
  sed -i "s/secrete: \"your-secret-key-here\"/secrete: \"$(escape_sed_replacement "${JNDC_SERVER_SECRET}")\"/" "${CONFIG_PATH}"
  sed -i "s/loginName: \"your_admin_username\"/loginName: \"$(escape_sed_replacement "${JNDC_ADMIN_USERNAME}")\"/" "${CONFIG_PATH}"
  sed -i "s/loginPassWord: \"your_admin_password\"/loginPassWord: \"$(escape_sed_replacement "${JNDC_ADMIN_PASSWORD}")\"/" "${CONFIG_PATH}"
  echo "Seeded default config to ${CONFIG_PATH} with bindIp=0.0.0.0"
  echo "Seeded default admin username from JNDC_ADMIN_USERNAME"
fi

if [ -f "${CONFIG_PATH}" ] && grep -q 'loginPassWord: "your_admin_password"' "${CONFIG_PATH}"; then
  echo "Warning: ${CONFIG_PATH} still uses the template admin password; update it before production use."
fi

if grep -q 'bindIp: "127.0.0.1"' "${CONFIG_PATH}"; then
  echo "Warning: ${CONFIG_PATH} still uses bindIp=127.0.0.1; Docker port publishing may not work."
fi

JAVA_OPTS="${JVM_XMS:--Xms512m} ${JVM_XMX:--Xmx1024m} ${JVM_METASPACE:--XX:MaxMetaspaceSize=256m} ${JVM_OPTS:-}"
if [ "${GC_LOG:-true}" = "true" ]; then
  JAVA_OPTS="${JAVA_OPTS} -Xlog:gc*:file=${LOG_DIR}/gc.log:time,uptime,level,tags:filecount=5,filesize=10M"
fi

echo "Starting jndc-server with config: ${CONFIG_PATH}"

exec java \
  -Duser.home="${RUNTIME_HOME}" \
  -Dapp.home="${WORKSPACE}" \
  -Dapp.mode=docker \
  ${JAVA_OPTS} \
  -classpath "${CLASSPATH}" \
  jndc_server.start.ServerStart \
  "${CONFIG_PATH}"
