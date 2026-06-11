#!/bin/bash
# ============================================================
# JNDC Server — 状态查询 (委托给 jndc.sh)
# ============================================================
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JNDC_SH="${SCRIPT_DIR}/jndc.sh"
if [ ! -x "$JNDC_SH" ]; then
  echo "[ERROR] 找不到 jndc.sh: $JNDC_SH" >&2
  exit 1
fi
exec "$JNDC_SH" status
