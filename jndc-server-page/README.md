# Halcyon-Admin

>Halcyon-Admin是一个基于Spring Boot3 和Vue3的通用后台管理系统。
>项目采用前后端分离的模式, 此仓库为前端项目。

## 开源地址

|        | 前端                                                  | 后端                                               |
| ------ | ----------------------------------------------------- | -------------------------------------------------- |
| Github | [halcyon-springboot](https://github.com/hhfb8848/halcyon-springboot) | [halcyon-admin-ui](https://github.com/hhfb8848/halcyon-admin-ui) |


## 项目结构

```shell  ✔
├─.github # github配置文件
│  └─workflows # 工作流，自动部署
├─.husky # 代码提交前校验配置文件
├─.vscode # IDE 工具推荐配置文件
├─build # 构建工具
├─mock # mock 模拟后台数据
├─public # 静态资源
│  └─map-geojson #地图资源
├─src
│  ├─api # 接口请求统一管理
│  │  ├─auth # 权限与账号
│  │  │  ├─permission
│  │  │  └─user-account
│  │  ├─big-screen # 数据大屏
│  │  └─system # 系统模块
│  │      ├─config # 配置管理
│  │      ├─dict # 字典管理
│  │      ├─file # 文件管理
│  │      ├─menu # 菜单管理
│  │      ├─notice # 通知公告管理
│  │      ├─role # 角色管理
│  │      └─user # 用户管理
│  ├─assets # 字体、图片等静态资源
│  ├─components  # 自定义通用组件
│  ├─config # 获取平台动态全局配置
│  ├─directives  # 自定义指令
│  │  ├─auth  # 按钮级别权限指令（根据路由meta中的auths字段进行判断）
│  │  ├─copy # 文本复制指令（默认双击复制）
│  │  ├─longpress # 长按指令
│  │  ├─optimize # 防抖、节流指令
│  │  └─ripple # 水波纹效果指令
│  ├─layout  # 主要页面布局
│  ├─plugins # 处理一些库或插件
│  ├─router # 路由配置
│  ├─store # pinia 状态管理
│  ├─style # 全局样式
│  ├─utils # 全局工具方法
│  └─views # 存放编写业务代码页面
│      ├─big-screen # 数据大屏
│      ├─error # 错误页面
│      ├─example # 例子
│      ├─login # 登录
│      ├─minitor # 系统监控模块（未实现）
│      ├─system # 系统模块
│      ├─user-account # 用户账号设置
│      └─welcome # 首页
└─types  # 全局 TS 类型配置
```

## vue-pure-admin
> 此项目基于vue-pure-admin进行二次开发。项目详细配置请查看以下文档

[点我查看 vue-pure-admin 文档](https://yiming_chang.gitee.io/pure-admin-doc)  
[点我查看 @pureadmin/utils 文档](https://pure-admin-utils.netlify.app)


## 在线预览

[查看预览](http://36.111.172.53:8848/)
账号密码：admin/admin123456
