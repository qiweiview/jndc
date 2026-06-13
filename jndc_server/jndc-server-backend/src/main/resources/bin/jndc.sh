#!/bin/bash
# ============================================================
#  JNDC Server — 统一管理脚本
#
#  用法:
#    ./jndc.sh start   [--dev]  启动服务
#    ./jndc.sh stop    [--force] 停止服务
#    ./jndc.sh restart [--dev]  重启服务
#    ./jndc.sh status           查看状态
#    ./jndc.sh logs    [-f]     查看日志
#
#  环境变量 (可选):
#    JAVA_HOME   JDK 路径，未设置则自动探测
#    JNDC_MODE   prod|dev，默认自动检测
#    JVM_OPTS    额外 JVM 参数
#
#  生产部署: 配合 jndc.env 和 jndc-server.service 使用
#  开发调试: ./jndc.sh start --dev
# ============================================================
set -euo pipefail

# ---- 应用元信息 ----
APP_NAME="jndc-server"
APP_MAIN="jndc_server.start.ServerStart"
JAVA_REQUIRED_MAJOR=21

# ---- 路径推导 ----
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_HOME="$(cd "${SCRIPT_DIR}/.." && pwd)"

# ---- 目录定义 ----
LOG_DIR="${APP_HOME}/logs"
CONF_DIR="${APP_HOME}/conf"
LIB_DIR="${APP_HOME}/lib"
PID_FILE="${APP_HOME}/bin/.app.pid"
LOG_FILE="${LOG_DIR}/bootstrap.out"
APP_LOG_FILE="${LOG_DIR}/info.operationLog"

# ---- 加载环境配置 (可选) ----
ENV_FILE="${APP_HOME}/bin/jndc.env"
[ -f "$ENV_FILE" ] && source "$ENV_FILE"

# ---- 模式检测 ----
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

# ---- 按模式设置默认值 ----
if [ "$RUN_MODE" = "prod" ]; then
  SHUTDOWN_TIMEOUT="${SHUTDOWN_TIMEOUT:-30}"
  : "${JVM_XMS:=-Xms512m}"
  : "${JVM_XMX:=-Xmx1024m}"
  : "${JVM_METASPACE:=-XX:MaxMetaspaceSize=256m}"
  : "${GC_LOG:=true}"
else
  SHUTDOWN_TIMEOUT="${SHUTDOWN_TIMEOUT:-10}"
  : "${JVM_XMS:=-Xms128m}"
  : "${JVM_XMX:=-Xmx256m}"
  : "${JVM_METASPACE:=-XX:MaxMetaspaceSize=128m}"
  : "${GC_LOG:=false}"
fi

CLASSPATH="${CONF_DIR}:${LIB_DIR}/*"

# ---- 构建 JVM 参数 ----
build_jvm_opts() {
  local opts="${JVM_XMS} ${JVM_XMX} ${JVM_METASPACE}"
  # GC 日志 (JDK 21)
  if [ "$GC_LOG" = "true" ]; then
    opts="$opts -Xlog:gc*:file=${LOG_DIR}/gc.log:time,uptime,level,tags:filecount=5,filesize=10M"
  fi
  # 用户自定义
  opts="$opts ${JVM_OPTS:-}"
  echo "$opts"
}

JVM_OPTS_STR="$(build_jvm_opts)"

# ============================================================
#  工具函数
# ============================================================

log() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"; }

die() { echo "[ERROR] $*" >&2; exit 1; }

rotate_bootstrap_log() {
  local max_size=$((5 * 1024 * 1024))
  local keep_files=5
  [ -f "$LOG_FILE" ] || return 0

  local current_size
  current_size="$(wc -c < "$LOG_FILE" 2>/dev/null || echo 0)"
  if [ "${current_size:-0}" -lt "$max_size" ]; then
    return 0
  fi

  local rotated
  rotated="${LOG_DIR}/bootstrap.$(date '+%Y%m%d%H%M%S').out"
  mv "$LOG_FILE" "$rotated"

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
      |_| \_|\___/|____/\____\____|  Server
EOF
  echo "  模式: ${RUN_MODE}  |  版本: $(date '+%Y%m%d')"
  echo ""
}

# ---- 探测 Java ----
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
    command -v java; return
  fi
  die "未找到 JDK ${JAVA_REQUIRED_MAJOR}+，请设置 JAVA_HOME 或将 java 加入 PATH"
}

java_major_version() {
  local java_cmd="$1"
  "$java_cmd" -version 2>&1 | awk -F '"' '/version/ {print $2; exit}' | awk -F. '{if ($1 == 1) print $2; else print $1}'
}

# ---- 读取 PID ----
get_pid() {
  [ -f "$PID_FILE" ] && cat "$PID_FILE" 2>/dev/null
}

# ---- 检查存活 ----
is_running() {
  local pid
  pid="$(get_pid)"
  [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null
}

# ---- 前置检查 ----
preflight() {
  local java_cmd="$1"
  [ -x "$java_cmd" ] || die "Java 不存在: $java_cmd"
  [ -d "$LIB_DIR" ]  || die "lib 目录不存在: $LIB_DIR"
  local ver
  ver="$("$java_cmd" -version 2>&1 | head -1)"
  local major_version
  major_version="$(java_major_version "$java_cmd")"
  [ -n "$major_version" ] || die "无法解析 Java 版本: $ver"
  if [ "$major_version" -lt "$JAVA_REQUIRED_MAJOR" ]; then
    die "检测到 Java ${major_version}，JNDC 需要 JDK ${JAVA_REQUIRED_MAJOR}+。请调整 JAVA_HOME 或 PATH。"
  fi
  log "Java: $ver"
  # 检查核心 JAR
  if ! ls "$LIB_DIR"/jndc_server*.jar &>/dev/null; then
    die "未找到 jndc_server.jar，请先执行 mvn package"
  fi
}

# ============================================================
#  核心命令
# ============================================================

do_start() {
  banner

  if is_running; then
    log "服务已在运行 (PID: $(get_pid))"
    return 0
  fi

  local java_cmd
  java_cmd="$(find_java)"
  preflight "$java_cmd"

  mkdir -p "$LOG_DIR"
  rotate_bootstrap_log

  log "启动中..."
  log "  主类:    $APP_MAIN"
  log "  JVM:     $JVM_OPTS_STR"
  log "  Classpath: $CLASSPATH"
  log "  业务日志: $APP_LOG_FILE"
  log "  引导日志: $LOG_FILE"

  # 后台启动，输出重定向到日志
  nohup "$java_cmd" \
    -Dapp.home="$APP_HOME" \
    -Dapp.mode="$RUN_MODE" \
    $JVM_OPTS_STR \
    -classpath "$CLASSPATH" \
    "$APP_MAIN" \
    >> "$LOG_FILE" 2>&1 &

  local pid=$!
  echo "$pid" > "$PID_FILE"

  # 等 2 秒确认存活
  sleep 2
  if kill -0 "$pid" 2>/dev/null; then
    log "启动成功 (PID: $pid)"
    return 0
  else
    rm -f "$PID_FILE"
    die "启动失败，进程已退出。查看日志: $LOG_FILE"
  fi
}

do_stop() {
  if ! is_running; then
    log "服务未运行"
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
  while [ $elapsed -lt "$timeout" ]; do
    if ! kill -0 "$pid" 2>/dev/null; then
      rm -f "$PID_FILE"
      log "已停止 (${elapsed}s)"
      return 0
    fi
    sleep 1
    elapsed=$((elapsed + 1))
    # 每 5 秒提示
    [ $((elapsed % 5)) -eq 0 ] && log "  等待中... (${elapsed}/${timeout}s)"
  done

  # 超时，强制终止
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
    echo ""
    echo "监听端口:"
    lsof -i -P -n 2>/dev/null | grep "$pid" | grep LISTEN || echo "  (无)"
    return 0
  else
    echo "${APP_NAME} 未运行"
    [ -f "$PID_FILE" ] && echo "  提示: 发现残留 PID 文件: $PID_FILE"
    return 1
  fi
}

do_logs() {
  local target_log="$APP_LOG_FILE"
  if [ ! -f "$target_log" ]; then
    target_log="$LOG_FILE"
  fi
  if [ ! -f "$target_log" ]; then
    die "日志文件不存在: $APP_LOG_FILE 或 $LOG_FILE"
  fi
  if [ "${1:-}" = "-f" ]; then
    tail -f "$target_log"
  else
    tail -50 "$target_log"
  fi
}

do_help() {
  cat <<EOF
JNDC Server 管理脚本

用法:
  $0 <command> [options]

命令:
  start   [--dev]     启动服务 (--dev 使用开发模式)
  stop    [--force]   停止服务 (--force 跳过优雅停机)
  restart [--dev]     重启服务
  status              查看运行状态
  logs    [-f]        查看日志 (-f 实时跟踪)
  help                显示帮助

环境变量:
  JAVA_HOME     JDK 路径 (默认自动探测)
  JNDC_MODE     prod|dev (默认自动检测)
  JVM_OPTS      额外 JVM 参数
  JVM_XMS       初始堆大小 (默认: prod 512m, dev 128m)
  JVM_XMX       最大堆大小 (默认: prod 1024m, dev 256m)
  JVM_METASPACE  Metaspace 上限
  SHUTDOWN_TIMEOUT 优雅停机超时秒数 (默认: prod 30, dev 10)

示例:
  $0 start              # 生产模式启动
  $0 start --dev        # 开发模式启动
  $0 stop               # 优雅停止
  $0 stop --force       # 强制停止
  $0 restart --dev      # 开发模式重启
  $0 logs -f            # 实时跟踪日志
  JVM_OPTS="-Dfoo=bar" $0 start  # 附加 JVM 参数
EOF
}

# ============================================================
#  参数解析
# ============================================================
COMMAND="${1:-help}"
shift || true

# --dev 参数处理
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
  help|-h|--help)  do_help ;;
  *)        echo "未知命令: $COMMAND"; do_help; exit 1 ;;
esac
