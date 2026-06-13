# JNDC Codex Guide

本文件是 Codex 在本仓库中的单一入口和单一事实源。

## 1. 项目北极星

**JNDC (Java No Distance Connection)** 是基于 Netty 的可视化内网穿透工具。

核心链路：

```text
外部请求 -> jndc server (隧道/代理) -> jndc client -> 本地服务
```

模块职责：

| 模块 | 职责 |
|---|---|
| `jndc_core` | 协议编解码、公共模型、Web 支撑、公共工具 |
| `jndc_server` | 服务端隧道、管理 API、HTTP 代理 |
| `jndc_client` | 客户端连接、服务注册、本地流量转发 |

## 2. 先看哪里

按任务类型优先从这些入口开始：

- 后端启动入口：`jndc_server/jndc-server-backend/src/main/java/jndc_server/start/ServerStart.java`
- 客户端启动入口：`jndc_client/jndc-client-backend/src/main/java/jndc_client/start/ClientStart.java`
- 运行目录解析：`jndc_core/src/main/java/jndc/utils/PathUtils.java`
- 协议与消息：`jndc_core/src/main/java/jndc/core/NDCMessageProtocol.java`、`jndc_core/src/main/java/jndc/core/NDCPCodec.java`、`jndc_core/src/main/java/jndc/core/message/`
- Server 管理 API：`jndc_server/jndc-server-backend/src/main/java/jndc_server/web_support/mapping/ServerManageMapping.java`
- Server HTTP 入口：`jndc_server/jndc-server-backend/src/main/java/jndc_server/web_support/http_module/JNDCHttpServer.java`
- Frontend 入口：`jndc_server/jndc-server-frontend/src/App.tsx`

深入说明见：

- `docs/codex/architecture.md`
- `docs/codex/dev-workflow.md`
- `docs/codex/task-playbook.md`

## 3. 本地事实

### 工具链

```bash
# JDK 21
export JAVA_HOME=/path/to/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"

# Maven
/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn
```

- Java 构建和运行统一按 JDK 21 处理。
- 前端优先使用 `pnpm`，不要切回 `npm`。
- 设备已有 `uv`，不要全局安装 Python 依赖。

### 运行目录和配置

- 运行时配置不从仓库内直接读取，而是从 `~/.jndc` 读取。
- Server 工作目录：`~/.jndc/server`
- Client 工作目录：`~/.jndc/client`
- Server 配置：`~/.jndc/server/conf/config.yml`
- Client 配置：`~/.jndc/client/conf/config.yml`
- 缺配置时，以各模块 `src/main/resources/conf/config.template.yml` 为模板。

### 默认端口

- 管理 API：`1777`
- TCP 隧道：`1081`
- HTTP 代理：`1080`
- Frontend `pnpm dev`：`5173`

## 4. 启动和联调

推荐启动顺序：

1. Server
2. Client
3. Frontend（仅调前端时）

常用命令：

```bash
# 后端整仓测试
/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn test

# 前端开发
cd jndc_server/jndc-server-frontend
pnpm install
pnpm dev

# 前端构建
pnpm build
```

部署脚本位于：

- `jndc_server/jndc-server-backend/src/main/resources/bin/jndc.sh`
- `jndc_server/jndc-server-backend/src/main/resources/bin/startup.sh`
- `jndc_server/jndc-server-backend/src/main/resources/bin/shutdown.sh`
- `jndc_server/jndc-server-backend/src/main/resources/bin/restart.sh`
- `jndc_server/jndc-server-backend/src/main/resources/bin/status.sh`
- `jndc_client/jndc-client-backend/src/main/resources/bin/jndc.sh`
- `jndc_client/jndc-client-backend/src/main/resources/bin/startup.sh`
- `jndc_client/jndc-client-backend/src/main/resources/bin/shutdown.sh`
- `jndc_client/jndc-client-backend/src/main/resources/bin/restart.sh`
- `jndc_client/jndc-client-backend/src/main/resources/bin/status.sh`
- `jndc_client/jndc-client-backend/src/main/resources/bin/jndc.ps1`
- `jndc_client/jndc-client-backend/src/main/resources/bin/jndc.bat`

## 5. 已知陷阱

- 以代码为准，不以旧 README 或历史说明为准。
- `PathUtils` 决定运行时配置目录是 `~/.jndc/...`，不是仓库内 `src/main/resources/conf/config.yml`。
- 仓库里有旧说明把前端开发端口写成 `778`，但当前 `vite.config.ts` 实际端口是 `5173`。
- 联调时优先核对 `~/.jndc` 下实际配置，不要只看仓库内模板。
- 管理端前端默认按独立部署处理；若走 nginx，代理 `/api` 到 `1777`，代理 `/ws` 到 `ws://<server>:1777/ws`。

## 6. 任务完成前最少验证清单

- Java 改动：
  - 运行 `mvn test`
  - 如果改了 server 或 client 启动链路，至少确认相关配置字段、主类、资源路径没有断
- Frontend 改动：
  - 在 `jndc_server/jndc-server-frontend` 下运行 `pnpm build`
  - 若改了 API 交互，确认代理路径仍是 `/api` 和 `/ws`
- 联调改动：
  - 确认 `~/.jndc/server/conf/config.yml` 与 `~/.jndc/client/conf/config.yml` 可被当前代码读取
  - 若联调管理前端，确认 nginx 或开发代理把 `/api` 和 `/ws` 转到 `1777`
- 脚本 / 部署改动：
  - 检查 `jndc.sh`、包装脚本、`jndc.env` 的路径和变量是否一致
  - 若改的是 server 部署，额外检查 `jndc-server.service`

## 7. 文档分工

- `AGENTS.md`：30 秒上手入口
- `docs/codex/architecture.md`：架构与改动定位
- `docs/codex/dev-workflow.md`：构建、联调、验证
- `docs/codex/task-playbook.md`：任务执行 playbook
- `README.md` / `README_zh_cn.md`：对外项目介绍，不作为 Codex 的主执行手册
