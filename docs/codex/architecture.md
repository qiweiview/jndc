# JNDC Architecture For Codex

## 1. 系统主链路

JNDC 的核心目标是把本地服务通过 client 注册到 server，再由 server 对外统一暴露。

主链路：

```text
外部请求
  -> jndc_server 监听入口
  -> server 根据端口 / 域名 / 管理规则定位目标服务
  -> 通过 NDC 私有协议把请求转发给 jndc_client
  -> client 再连接本地 serviceIp:servicePort
  -> 响应沿原路径返回
```

分两类入口理解最有效：

- TCP / 隧道链路：client 与 server 的注册、保活、转发
- HTTP / 管理链路：管理 API、WebSocket、域名路由、HTTP 代理

## 2. 模块边界

### `jndc_core`

负责跨端共用能力，改动这里通常意味着 server 和 client 都可能受影响。

重点看：

- `jndc.core.NDCMessageProtocol`
- `jndc.core.NDCPCodec`
- `jndc.core.message.*`
- `jndc.utils.PathUtils`
- `jndc.web_support.*`

这里承载：

- NDC 协议头和编解码
- 公共消息模型
- 运行目录与配置读取
- 管理 API / Web 的基础设施
- SQLite 与公共工具类

### `jndc_server`

负责对外暴露服务和管理控制面。

重点看：

- 启动入口：`jndc_server.start.ServerStart`
- 主应用：`jndc_server.core.JNDCServerApp`
- 隧道消息处理：`jndc_server.core.app.JNDCServerMessageHandle`
- 端口转发：`jndc_server.core.port_app.*`
- 管理 API：`jndc_server.web_support.mapping.ServerManageMapping`
- HTTP 服务：`jndc_server.web_support.http_module.JNDCHttpServer`

这里承载：

- client 注册与在线状态
- 端口监听与服务绑定
- IP 黑白名单和时间规则
- 管理 API
- HTTP 路由与代理

### `jndc_client`

负责连接 server 并转发到本地服务。

重点看：

- 启动入口：`jndc_client.start.ClientStart`
- 主应用：`jndc_client.core.JNDClientApp`
- 消息处理：`jndc_client.core.JNDCClientMessageHandle`
- 服务提供：`jndc_client.core.port_app.*`
- 配置中心：`jndc_client.core.JNDCClientConfig*`

这里承载：

- server 连接建立
- client ID 和注册信息加载
- 本地服务列表上报
- 本地 TCP 连接与数据转发

## 3. 常见改动面怎么定位

### 协议或消息结构改动

先看：

- `jndc_core/src/main/java/jndc/core/NDCMessageProtocol.java`
- `jndc_core/src/main/java/jndc/core/NDCPCodec.java`
- `jndc_core/src/main/java/jndc/core/message/`

联动面：

- server 消息处理
- client 消息处理
- 可能影响序列化、拆包、兼容性

### Server 隧道 / 端口 / 过滤规则改动

先看：

- `jndc_server.core.NDCServerConfigCenter`
- `jndc_server.core.ChannelHandlerContextHolder`
- `jndc_server.core.port_app.ServerPortProtector`
- `jndc_server.core.filter.*`

联动面：

- 管理 API 列表与操作
- 数据库存储对象
- 前端管理页展示

### Client 注册 / 连接 / 转发改动

先看：

- `jndc_client.core.JNDCClientConfig`
- `jndc_client.core.JNDCClientConfigCenter`
- `jndc_client.core.JNDCClientMessageHandle`
- `jndc_client.core.port_app.ClientTCPDataHandle`

联动面：

- 注册消息结构
- server 侧在线状态
- 本地服务可达性

### 管理 API / HTTP 路由 / 前端页面改动

先看：

- `jndc_server.web_support.mapping.ServerManageMapping`
- `jndc_server.web_support.http_module.*`
- `jndc_server/jndc-server-frontend/src/api/*`
- `jndc_server/jndc-server-frontend/src/pages/*`

联动面：

- `ServerUrlConstant` 中的路径常量
- 前端 `request.ts` 的 `/api` 代理约定
- 前端独立部署时的 `/ws` 反向代理
- 页面表格 / 表单的数据结构

## 4. 关键类索引

高频入口只记这些就够：

- `ServerStart`：server 读配置并启动
- `ClientStart`：client 读配置并启动
- `PathUtils`：运行目录真实来源
- `JNDCServerApp` / `JNDClientApp`：两端应用装配
- `NDCMessageProtocol` / `NDCPCodec`：协议核心
- `ServerManageMapping`：管理 API 主入口
- `JNDCHttpServer`：HTTP 入口
- `App.tsx`：前端路由与主题入口

## 5. 读代码顺序建议

- 改协议：`jndc_core` -> server message handle -> client message handle
- 改 server 行为：`ServerStart` -> `JNDCServerApp` -> 具体 handler / mapping / port_app
- 改 client 行为：`ClientStart` -> `JNDClientApp` -> config / message handle / port_app
- 改前端：`App.tsx` -> `src/api/*` -> `src/pages/*` -> 对应后端 mapping
