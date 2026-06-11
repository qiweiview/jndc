# JNDC Web - 内网穿透管理系统前端

基于 Vite + React + Ant Design + TypeScript 构建的 JNDC 内网穿透管理系统前端。

## 技术栈

- **构建工具**: Vite 8.x
- **前端框架**: React 19.x
- **UI 组件库**: Ant Design 6.x
- **类型系统**: TypeScript 6.x
- **路由**: React Router 7.x
- **HTTP 客户端**: Axios
- **状态管理**: Zustand

## 功能模块

- **登录认证**: 用户名/密码登录，Token 认证
- **隧道管理**: 查看活跃隧道、发送心跳、断开连接、查看断开记录
- **服务注册**: 查看已注册的服务列表
- **端口监听**: 创建/删除端口监听、绑定服务、设置时间范围
- **IP 访问管控**: 黑白名单管理、IP 记录查看与清理
- **HTTP 应用**: 域名路由规则管理（转发/重定向/固定响应）
- **WebSocket**: 实时通知推送

## 开发

```bash
# 安装依赖
pnpm install

# 启动开发服务器 (端口: 778)
pnpm dev

# 构建生产版本
pnpm build

# 预览生产构建
pnpm preview
```

## 代理配置

开发模式下，API 请求会自动代理到后端服务：

- `/api/*` → `http://localhost:777/*`
- `/ws` → `ws://localhost:777/ws`

## 项目结构

```
src/
├── api/            # API 请求封装
│   ├── auth.ts     # 认证相关
│   ├── channel.ts  # 隧道管理
│   ├── httpApp.ts  # HTTP 应用
│   ├── ipFilter.ts # IP 过滤
│   ├── port.ts     # 端口管理
│   └── service.ts  # 服务管理
├── components/     # 公共组件
│   └── Layout/     # 主布局
├── pages/          # 页面组件
│   ├── login/      # 登录页
│   ├── channel/    # 隧道列表
│   ├── service/    # 服务列表
│   ├── port/       # 端口监听
│   ├── ipFilter/   # IP 过滤
│   └── httpApp/    # HTTP 应用
├── stores/         # 状态管理
├── styles/         # 全局样式
├── types/          # TypeScript 类型定义
├── utils/          # 工具函数
│   ├── request.ts  # Axios 封装
│   └── websocket.ts # WebSocket 客户端
├── App.tsx         # 路由配置
└── main.tsx        # 入口文件
```

## 与后端集成

构建产物 `dist/` 目录可以部署到 JNDC Server 的静态资源目录，或通过 Nginx 等反向代理提供服务。
