# JNDC Dev Workflow

## 1. 本地工具链

```bash
# JDK 8
/Users/liuqiwei/data/jdk1.8.0/Contents/Home/bin/java

# Maven
/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn
```

约束：

- Java 相关构建、运行默认按 JDK 8 处理
- 前端优先 `pnpm`
- Python 工具优先 `uv`

## 2. 配置与运行目录

代码实际通过 `PathUtils` 读取：

- `~/.jndc/server/conf/config.yml`
- `~/.jndc/client/conf/config.yml`

仓库内模板：

- `jndc_server/jndc-server-backend/src/main/resources/conf/config.template.yml`
- `jndc_client/jndc-client-backend/src/main/resources/conf/config.template.yml`

仓库内 `config.yml` 和模板主要用于打包资源、参考示例，不应替代 `~/.jndc` 作为运行真相。

## 3. 常用命令

### Java

```bash
cd /Users/liuqiwei/IdeaProjects/jndc
/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn test
```

如果只想先验证某个聚合模块：

```bash
/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn -pl jndc_server -am test
/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn -pl jndc_client -am test
```

### Frontend

```bash
cd /Users/liuqiwei/IdeaProjects/jndc/jndc_server/jndc-server-frontend
pnpm install
pnpm dev
pnpm build
```

当前 `vite.config.ts` 实际开发端口是 `5173`，并代理：

- `/api/*` -> `http://localhost:1777/*`
- `/ws` -> `ws://localhost:1777/ws`

## 4. 启动顺序

推荐顺序：

1. Server
2. Client
3. Frontend

典型联调端口：

- 管理 API：`1777`
- TCP 隧道：`1081`
- HTTP 服务：`1080`
- Vite Dev Server：`5173`

## 5. 前端静态资源

当前可工作的事实流程：

```bash
cd /Users/liuqiwei/IdeaProjects/jndc/jndc_server/jndc-server-frontend
pnpm build

cd /Users/liuqiwei/IdeaProjects/jndc
rm -rf jndc_server/jndc-server-backend/target/jndc_server/page
cp -r jndc_server/jndc-server-frontend/dist jndc_server/jndc-server-backend/target/jndc_server/page
```

Server 启动时需要的静态资源目录是：

- 发布目录下的 `page`
- 开发态兼容旧目录名 `html` / `compare_dist`

## 6. 日志、脚本与部署

部署脚本源码目录：

- `jndc_server/jndc-server-backend/src/main/resources/bin/`

关键文件：

- `jndc.sh`
- `startup.sh`
- `shutdown.sh`
- `restart.sh`
- `status.sh`
- `jndc.env`
- `jndc-server.service`

开发模式常用命令：

```bash
cd jndc_server/jndc-server-backend/target/jndc_server/bin
./jndc.sh start --dev
./jndc.sh stop
./jndc.sh restart --dev
./jndc.sh status
./jndc.sh logs -f
```

## 7. 做完不同类型改动后怎么验

### 仅后端 Java 改动

- 跑 `mvn test`
- 如果改了配置解析、启动入口、路径解析，额外核对 `ServerStart` / `ClientStart` / `PathUtils`
- 如果改了 API，核对前端调用路径是否仍匹配

### 仅前端改动

- 跑 `pnpm build`
- 核对 `src/api/*` 与后端路径常量是否对应
- 如果依赖 server 承载静态页，确认 `dist` 已同步到发布目录的 `page`

### 全链路联调改动

- 确认 `~/.jndc/server/conf/config.yml` 和 `~/.jndc/client/conf/config.yml` 已就绪
- 启动 server
- 启动 client
- 启动前端或访问 server 承载的静态页
- 至少验证一条管理端操作能成功触发后端行为

### 启停脚本 / 部署改动

- 检查包装脚本是否仍委托到 `jndc.sh`
- 检查 `JAVA_HOME`、`APP_HOME`、`CONF_DIR`、`LIB_DIR`、`PID_FILE` 等路径是否自洽
- 检查 dev / prod 模式判断是否仍成立

## 8. 当前已知不一致

- 旧说明里出现过前端端口 `778`，代码实际是 `5173`
- 运行时以 `~/.jndc` 为准，不以仓库内模板和旧 README 为准
