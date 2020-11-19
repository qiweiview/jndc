if [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
  JAVA="$JAVA_HOME/bin/java"
elif type -p java; then
  JAVA=java
else
  echo "Error: JAVA_HOME is not set and java could not be found in PATH." 1>&2
  exit 1
fi

NDC="${BASH_SOURCE-$0}"
NDC="$(dirname "${NDC}")"
NDCDIR="$(
  cd "${ZOOBIN}"
  pwd
)"
CONIFG="$NDCDIR/config.yml"
LIB="$NDCDIR/jndc-1.0.jar"

nohup "$JAVA" -jar "$LIB" "$CONIFG" "CLIENT_APP_TYPE" "jndcccccccccc_client" &

echo 'start jndc success'


