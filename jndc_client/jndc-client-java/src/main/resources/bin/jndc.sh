#!/bin/bash
# ============================================================
#  JNDC Client — 统一管理脚本
#
#  用法:
#    ./jndc.sh start   [--dev]   启动客户端
#    ./jndc.sh stop    [--force] 停止客户端
#    ./jndc.sh restart [--dev]   重启客户端
#    ./jndc.sh status            查看状态
#    ./jndc.sh logs    [-f]      查看日志
# ============================================================
set -euo pipefail

APP_NAME="jndc-client"
APP_MAIN="jndc_client.start.ClientStart"
JAVA_REQUIRED_MAJOR=21

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_HOME="$(cd "${SCRIPT_DIR}/.." && pwd)"

LOG_DIR="${APP_HOME}/logs"
CONF_DIR="${APP_HOME}/conf"
LIB_DIR="${APP_HOME}/lib"
PID_FILE="${APP_HOME}/bin/.app.pid"
BOOTSTRAP_LOG="${LOG_DIR}/bootstrap.out"
APP_LOG_FILE="${APP_HOME}/output.log"
ENV_FILE="${APP_HOME}/bin/jndc.env"
RUNTIME_CONF_DIR="${HOME}/.jndc/client/conf"
RUNTIME_CONFIG_FILE="${RUNTIME_CONF_DIR}/config.yml"
TEMPLATE_CONFIG_FILE="${CONF_DIR}/config.template.yml"

[ -f "$ENV_FILE" ] && source "$ENV_FILE"

detect_mode() {
  if [ -n "${JNDC_MODE:-}" ]; then
    echo "$JNDC_MODE"
    return
  fi
  if echo "$APP_HOME" | grep -qE '/target/|/build/'; then
    echo "dev"
  else
    echo "prod"
  fi
}

RUN_MODE="$(detect_mode)"

if [ "$RUN_MODE" = "prod" ]; then
  SHUTDOWN_TIMEOUT="${SHUTDOWN_TIMEOUT:-30}"
  : "${JVM_XMS:=-Xms256m}"
  : "${JVM_XMX:=-Xmx512m}"
  : "${JVM_METASPACE:=-XX:MaxMetaspaceSize=192m}"
  : "${GC_LOG:=false}"
else
  SHUTDOWN_TIMEOUT="${SHUTDOWN_TIMEOUT:-10}"
  : "${JVM_XMS:=-Xms128m}"
  : "${JVM_XMX:=-Xmx256m}"
  : "${JVM_METASPACE:=-XX:MaxMetaspaceSize=128m}"
  : "${GC_LOG:=false}"
fi

CLASSPATH="${CONF_DIR}:${LIB_DIR}/*"

build_jvm_opts() {
  local opts="${JVM_XMS} ${JVM_XMX} ${JVM_METASPACE}"
  if [ "$GC_LOG" = "true" ]; then
    opts="$opts -Xlog:gc*:file=${LOG_DIR}/gc.log:time,uptime,level,tags:filecount=5,filesize=10M"
  fi
  opts="$opts ${JVM_OPTS:-}"
  echo "$opts"
}

JVM_OPTS_STR="$(build_jvm_opts)"

log() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"; }

die() { echo "[ERROR] $*" >&2; exit 1; }

rotate_bootstrap_log() {
  local max_size=$((5 * 1024 * 1024))
  local keep_files=5
  [ -f "$BOOTSTRAP_LOG" ] || return 0

  local current_size
  current_size="$(wc -c < "$BOOTSTRAP_LOG" 2>/dev/null || echo 0)"
  if [ "${current_size:-0}" -lt "$max_size" ]; then
    return 0
  fi

  local rotated
  rotated="${LOG_DIR}/bootstrap.$(date '+%Y%m%d%H%M%S').out"
  mv "$BOOTSTRAP_LOG" "$rotated"

  local count=0
  while IFS= read -r file; do
    count=$((count + 1))
    if [ "$count" -gt "$keep_files" ]; then
      rm -f "$file"
    fi
  done < <(find "$LOG_DIR" -maxdepth 1 -type f -name 'bootstrap.*.out' | sort -r)
}

banner() {
  cat <<'EOF'
       __  _   _ ____   ____ ____
      |  \| | | |  _ \ / ___/ ___|
      | | | | | | | | | |  | |
      | |\  | |_| | |_| |__| |___
      |_| \_|\___/|____/\____\____|  Client
EOF
  echo "  模式: ${RUN_MODE}  |  版本: $(date '+%Y%m%d')"
  echo ""
}

find_java() {
  if [ -n "${JAVA_HOME:-}" ]; then
    local explicit_java="${JAVA_HOME}/bin/java"
    [ -x "$explicit_java" ] && echo "$explicit_java" && return
  fi
  local candidates=(
    "/usr/lib/jvm/java-21-openjdk/bin/java"
    "/usr/lib/jvm/java-21-openjdk-amd64/bin/java"
    "/usr/lib/jvm/temurin-21-jdk/bin/java"
    "/usr/lib/jvm/jdk-21/bin/java"
    "/usr/local/jdk21/bin/java"
    "/opt/homebrew/opt/openjdk@21/bin/java"
    "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/java"
    "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home/bin/java"
  )
  for c in "${candidates[@]}"; do
    [ -x "$c" ] && echo "$c" && return
  done
  if command -v java &>/dev/null; then
    command -v java
    return
  fi
  die "未找到 JDK ${JAVA_REQUIRED_MAJOR}+，请设置 JAVA_HOME 或将 java 加入 PATH"
}

java_major_version() {
  local java_cmd="$1"
  "$java_cmd" -version 2>&1 | awk -F '"' '/version/ {print $2; exit}' | awk -F. '{if ($1 == 1) print $2; else print $1}'
}

get_pid() {
  [ -f "$PID_FILE" ] && tr -d '[:space:]' < "$PID_FILE" 2>/dev/null
}

is_running() {
  local pid
  pid="$(get_pid)"
  [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null
}

require_runtime_config() {
  if [ -f "$RUNTIME_CONFIG_FILE" ]; then
    return 0
  fi
  mkdir -p "$RUNTIME_CONF_DIR"
  die "缺少运行配置: $RUNTIME_CONFIG_FILE，请先参考 ${TEMPLATE_CONFIG_FILE} 创建 config.yml"
}

preflight() {
  local java_cmd="$1"
  [ -x "$java_cmd" ] || die "Java 不存在: $java_cmd"
  [ -d "$LIB_DIR" ] || die "lib 目录不存在: $LIB_DIR"
  [ -d "$CONF_DIR" ] || die "conf 目录不存在: $CONF_DIR"
  require_runtime_config

  local ver major_version
  ver="$("$java_cmd" -version 2>&1 | head -1)"
  major_version="$(java_major_version "$java_cmd")"
  [ -n "$major_version" ] || die "无法解析 Java 版本: $ver"
  if [ "$major_version" -lt "$JAVA_REQUIRED_MAJOR" ]; then
    die "检测到 Java ${major_version}，JNDC 需要 JDK ${JAVA_REQUIRED_MAJOR}+。请调整 JAVA_HOME 或 PATH。"
  fi
  log "Java: $ver"
  if ! ls "$LIB_DIR"/jndc_client*.jar &>/dev/null; then
    die "未找到 jndc_client.jar，请先执行 mvn package"
  fi
}

do_start() {
  banner

  if is_running; then
    log "客户端已在运行 (PID: $(get_pid))"
    return 0
  fi

  local java_cmd
  java_cmd="$(find_java)"
  preflight "$java_cmd"

  mkdir -p "$LOG_DIR"
  rotate_bootstrap_log

  log "启动中..."
  log "  主类:      $APP_MAIN"
  log "  JVM:       $JVM_OPTS_STR"
  log "  Classpath: $CLASSPATH"
  log "  运行配置:  $RUNTIME_CONFIG_FILE"
  log "  业务日志:  $APP_LOG_FILE"
  log "  引导日志:  $BOOTSTRAP_LOG"

  pushd "$APP_HOME" >/dev/null
  nohup "$java_cmd" \
    -Dapp.home="$APP_HOME" \
    -Dapp.mode="$RUN_MODE" \
    $JVM_OPTS_STR \
    -classpath "$CLASSPATH" \
    "$APP_MAIN" \
    >> "$BOOTSTRAP_LOG" 2>&1 &
  local pid=$!
  popd >/dev/null

  echo "$pid" > "$PID_FILE"

  sleep 2
  if kill -0 "$pid" 2>/dev/null; then
    log "启动成功 (PID: $pid)"
    return 0
  fi

  rm -f "$PID_FILE"
  die "启动失败，进程已退出。查看日志: $BOOTSTRAP_LOG"
}

do_stop() {
  if ! is_running; then
    log "客户端未运行"
    rm -f "$PID_FILE"
    return 0
  fi

  local pid timeout
  pid="$(get_pid)"
  timeout="$SHUTDOWN_TIMEOUT"

  if [ "${1:-}" = "--force" ]; then
    log "强制停止 (PID: $pid)"
    kill -9 "$pid" 2>/dev/null || true
    rm -f "$PID_FILE"
    log "已停止"
    return 0
  fi

  log "优雅停止 (PID: $pid, 超时: ${timeout}s)..."
  kill -15 "$pid" 2>/dev/null || true

  local elapsed=0
  while [ "$elapsed" -lt "$timeout" ]; do
    if ! kill -0 "$pid" 2>/dev/null; then
      rm -f "$PID_FILE"
      log "已停止 (${elapsed}s)"
      return 0
    fi
    sleep 1
    elapsed=$((elapsed + 1))
    [ $((elapsed % 5)) -eq 0 ] && log "  等待中... (${elapsed}/${timeout}s)"
  done

  log "优雅停止超时，强制终止"
  kill -9 "$pid" 2>/dev/null || true
  sleep 1
  rm -f "$PID_FILE"
  log "已强制停止"
}

do_restart() {
  do_stop
  sleep 1
  do_start
}

do_status() {
  if is_running; then
    local pid
    pid="$(get_pid)"
    echo "${APP_NAME} 正在运行 (PID: $pid)"
    echo ""
    echo "进程信息:"
    ps -p "$pid" -o pid,ppid,%cpu,%mem,etime,command 2>/dev/null || true
    return 0
  fi

  echo "${APP_NAME} 未运行"
  [ -f "$PID_FILE" ] && echo "  提示: 发现残留 PID 文件: $PID_FILE"
  return 1
}

do_logs() {
  local target_log="$APP_LOG_FILE"
  if [ ! -f "$target_log" ]; then
    target_log="$BOOTSTRAP_LOG"
  fi
  [ -f "$target_log" ] || die "日志文件不存在: $APP_LOG_FILE 或 $BOOTSTRAP_LOG"

  if [ "${1:-}" = "-f" ]; then
    tail -f "$target_log"
  else
    tail -50 "$target_log"
  fi
}

do_help() {
  cat <<EOF
JNDC Client 管理脚本

用法:
  $0 <command> [options]

命令:
  start   [--dev]     启动客户端 (--dev 使用开发模式)
  stop    [--force]   停止客户端 (--force 跳过优雅停机)
  restart [--dev]     重启客户端
  status              查看运行状态
  logs    [-f]        查看日志 (-f 实时跟踪)
  help                显示帮助

环境变量:
  JAVA_HOME          JDK 路径 (默认自动探测)
  JNDC_MODE          prod|dev (默认自动检测)
  JVM_OPTS           额外 JVM 参数
  JVM_XMS            初始堆大小 (默认: prod 256m, dev 128m)
  JVM_XMX            最大堆大小 (默认: prod 512m, dev 256m)
  JVM_METASPACE      Metaspace 上限
  SHUTDOWN_TIMEOUT   优雅停机超时秒数 (默认: prod 30, dev 10)

运行前置:
  运行配置读取自: $RUNTIME_CONFIG_FILE
  若文件不存在，请参考: $TEMPLATE_CONFIG_FILE
EOF
}

COMMAND="${1:-help}"
shift || true

for arg in "$@"; do
  case "$arg" in
    --dev) export JNDC_MODE=dev ;;
  esac
done

case "$COMMAND" in
  start)    do_start   "$@" ;;
  stop)     do_stop    "$@" ;;
  restart)  do_restart "$@" ;;
  status)   do_status  "$@" ;;
  logs)     do_logs    "$@" ;;
  help|-h|--help) do_help ;;
  *) echo "未知命令: $COMMAND"; do_help; exit 1 ;;
esac
