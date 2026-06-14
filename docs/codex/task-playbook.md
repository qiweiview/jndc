# JNDC Task Playbook

本文件面向 Codex，目标是让常见任务从“定位”到“最低验证”都可直接执行。

## 1. 协议 / 核心模型改动

先读：

- `jndc_core/src/main/java/jndc/core/NDCMessageProtocol.java`
- `jndc_core/src/main/java/jndc/core/NDCPCodec.java`
- `jndc_core/src/main/java/jndc/core/message/`

推荐顺序：

1. 明确协议字段或消息对象怎么变
2. 改 `jndc_core`
3. 检查 server 消息消费端
4. 检查 client 消息消费端
5. 检查是否影响前端展示字段

容易漏掉：

- 双端兼容性
- 自动拆包 / 长度限制
- 旧消息字段被前端或数据库间接依赖

最低验证：

- `mvn test`
- 搜索被改消息类型在 server/client 的所有使用点

## 2. Server 管理端或 HTTP 代理改动

先读：

- `jndc_server.web_support.mapping.ServerManageMapping`
- `jndc_server.web_support.http_module.JNDCHttpServer`
- `jndc_server.web_support.http_module.HostRouteHandle`
- `jndc_server.web_support.http_module.LiteHttpProxy*`

推荐顺序：

1. 明确改的是管理 API、HTTP 路由还是代理转发
2. 调整后端 mapping / handler / DTO
3. 如果前端受影响，再改 `src/api/*` 和页面
4. 核对路径常量和响应结构

容易漏掉：

- `ResponseMessage` 风格和直接返回对象风格并存
- `request.ts` 对 `code` 字段有特殊处理
- WebSocket / 认证 token / 路由常量联动

最低验证：

- `mvn test`
- 若前端受影响，运行 `pnpm build`
- 检查 `/api` 代理后的真实路径仍匹配后端 mapping

## 3. Client 注册 / 连接改动

先读：

- `jndc_client.start.ClientStart`
- `jndc_client.core.JNDCClientConfig`
- `jndc_client.core.JNDCClientConfigCenter`
- `jndc_client.core.JNDCClientMessageHandle`
- `jndc_client.core.port_app.*`

推荐顺序：

1. 先明确配置来源是否变化
2. 修改注册、连接或本地转发逻辑
3. 回查 server 对应的注册消费逻辑
4. 核对超时、clientId、服务列表加载

容易漏掉：

- `~/.jndc/client/conf/config.yml` 才是运行时配置
- `client_id` 与自动加载逻辑
- server 对 client 注册结果的假设

最低验证：

- `mvn test`
- 搜索 client 注册消息在 server 侧的消费路径

## 4. 前端页面 / API 联调改动

先读：

- `jndc_server/jndc-server-frontend/src/App.tsx`
- `jndc_server/jndc-server-frontend/src/api/*`
- `jndc_server/jndc-server-frontend/src/pages/*`
- 对应后端 `ServerManageMapping` 或其他 mapping

推荐顺序：

1. 先定后端路径和返回结构
2. 再改 `src/api/*`
3. 最后改页面和状态管理
4. 如需生产联调，再核对 nginx 对 `/api` 和 `/ws` 的代理

容易漏掉：

- `baseURL` 固定是 `/api`
- 401 会被前端拦截器重定向到 `/login`
- `pnpm dev` 实际端口是 `5173`
- 页面改完后，还要确认 nginx 或开发代理路径没有偏移

最低验证：

- `pnpm build`
- 若后端同步改动，再跑 `mvn test`
- 如需 nginx 联调，确认 `/api` 和 `/ws` 都能正确转发到 `1777`

## 5. 启停脚本 / 部署改动

先读：

- `jndc_server/jndc-server-backend/src/main/resources/bin/jndc.sh`
- `jndc_client/jndc-client-java/src/main/resources/bin/jndc.sh`
- `startup.sh`
- `shutdown.sh`
- `restart.sh`
- `status.sh`
- `jndc.env`
- `jndc-server.service`

推荐顺序：

1. 优先改 `jndc.sh`
2. 再检查包装脚本是否只做委托
3. 最后核对 service 文件和环境变量说明

容易漏掉：

- dev / prod 自动检测依赖路径中是否包含 `/target/` 或 `/build/`
- 默认 `JAVA_HOME` 在 dev / prod 下不同
- `CLASSPATH`、`PID_FILE`、日志路径是否一起更新
- Windows 包装脚本是否仍正确委托到 `jndc.ps1`

最低验证：

- 人工检查脚本变量展开是否自洽
- 确认包装脚本仍能委托到 `jndc.sh`
- 如改部署说明，确保与实际脚本参数一致
