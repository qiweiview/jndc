![J NDC](https://s1.ax1x.com/2020/11/04/B6HETJ.png)
![jdk21](https://img.shields.io/badge/jdk-21-orange.svg)


## [English Document](https://github.com/qiweiview/jndc/blob/master/README.md)

## 使用边界
JNDC 是一个社区维护的内网穿透与代理项目。它具备内网服务暴露、流量转发、服务端集中管理等能力，若部署方式不当，可能带来明显的安全风险。

如果你准备将它用于企业环境或类生产环境，请先自行完成安全评估、权限设计、网络隔离、审计留痕、密钥管理、备份与回滚方案设计。本仓库不提供 SLA、不提供安全合规背书，也不保证适用于任何特定业务环境。

本仓库继续采用 Apache-2.0 许可证，不额外增加与开源许可证冲突的使用领域限制。部署前请先阅读 [DISCLAIMER.md](https://github.com/qiweiview/jndc/blob/master/DISCLAIMER.md)、[SUPPORT.md](https://github.com/qiweiview/jndc/blob/master/SUPPORT.md) 和 [SECURITY.md](https://github.com/qiweiview/jndc/blob/master/SECURITY.md)。



## 项目介绍
* "J NDC" 是 "java no distance connection"的缩写，意在提供简便易用的可视化内网穿透应用，应用基于java netty编写。
* 应用以Client/Server架构构建,通过"服务注册"思路，由本地client端向server端提供本地服务，由server端管理暴露对应服务 
* 应用核心由ndc私有协议支撑，提供了"传输数据加密","ip黑白名单","客户端可视化","服务端口定时","域名路由"功能
* 当前构建和运行均要求 JDK 21。执行 Maven 或启动脚本前，请先将 `JAVA_HOME` 指向 JDK 21。
* 管理端前端和管理 API 现在只保留在服务端，客户端不再提供独立管理控制台。
* 项目源码目录结构
```
- jndc
 - jndc_core # 核心公共实现
 - jndc_server # 服务端实现
 - jndc_client # 客户端实现
```

* TCP数据流向
```
broser     ------->               (tunnel)               ---------->local_app
client     -------> jndc server <----------> jndc client ---------->local_app
other      ------->                                      ---------->local_app
```

## 项目使用范例
* [范例](https://github.com/qiweiview/jndc/blob/master/tutorial.md)


## 协议说明
* NDC协议
* 协议设计为仅支持ipv4
* 单包数据长度限制,超出将自动拆包
```
public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024
```

* 协议说明：
```
--------------------------------
  3byte      1byte      1byte
|  ndc   |  version  |  type   |
--------------------------------
            4byte
|          local ip            |
--------------------------------
            4byte
|          remote ip           |
--------------------------------
            4byte
|          local port          |
--------------------------------
            4byte
|          server port         |
--------------------------------
            4byte
|          remote port         |
--------------------------------
            4byte
|          data length         |
--------------------------------
           data length byte
|            data              |
--------------------------------
```

## 配置文件说明

### server 配置
```yaml
secrete: "xxx" # 服务端密钥，非常重要务必在使用前更改
loglevel: "info"
servicePort: 1081 # jndc服务端运行监听端口
bindIp: "127.0.0.1" # jndc服务端运行ip

cleanupConfig: # 运行时数据清理配置
  enabled: true
  runIntervalHours: 24
  channelRecordRetentionDays: 30
  ipFilterRecordRetentionDays: 30
  trafficTrendMinuteRetentionDays: 3
  trafficTrendHourRetentionDays: 14
  trafficTrendDayRetentionDays: 90
  trafficTrendMonthRetentionDays: 1095
  vacuumAfterCleanup: true

manageConfig: # 管理端api服务
  managementApiPort: 1777 #管理api端口
  useSsl: false # 是否使用ssl
  jksPath: "/xx.jks" #jks 证书地址
  jksPass: "xxx" # jks 证书密码
  loginName: "xxx" # 登录用户名
  loginPassWord: "xxx" # 登录密码

webConfig: # http web服务
  notFoundPage: "path/to/your/404.html"
  httpPort: 1080 # http应用端口
  useSsl: false # 是否使用ssl
  jksPath: "/xx.jks" #jks 证书地址
  jksPass: "ddd" # jks 证书密码
```

### client 配置
```yaml
secrete: "xxx1" # 服务端密钥，很重要务必在使用前更改
loglevel: "info" # 日志打印等级
serverIp: "127.0.0.1" # 服务端运行监听ip
serverPort: "1081" # 服务端运行端口
autoReleaseTimeOut: 600000 # 客户端自动断开时间（毫秒）
authMode: 0 # 0=SELF_MANAGED(自管理), 1=FULL_AUTHORIZED(全授权受服务端控制)
clientServiceDescriptions: # 注册服务
  - serviceName: "xx"
    serviceIp: "xx.com"
    servicePort: "8080"
    serviceEnable: true
```

客户端配置已不再包含 `manageConfig`，所有管理操作统一走服务端管理前端和管理 API。

## 小结
* 如若有好的功能需求，或代码存在的bug欢迎在issue里提出

## 安全提示
* 不要在缺少独立鉴权、传输保护和网络层访问控制的情况下，直接将管理 API 或 Web 入口暴露到公网。
* 任何模板中的密钥、密码、证书路径都应在实际部署前替换，若曾经以明文共享，应立即轮换。
* `FULL_AUTHORIZED`、远程管理能力、隧道与代理能力都应视为高权限能力，需要明确审批和最小权限控制。
* 如果你是新手，或正在评估企业使用，请先在隔离测试环境验证，不要直接进入生产网络。

## 许可证与策略
* 许可证：[Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0)
* 风险与免责边界：[DISCLAIMER.md](https://github.com/qiweiview/jndc/blob/master/DISCLAIMER.md)
* 社区支持策略：[SUPPORT.md](https://github.com/qiweiview/jndc/blob/master/SUPPORT.md)
* 安全漏洞报告策略：[SECURITY.md](https://github.com/qiweiview/jndc/blob/master/SECURITY.md)



## supporting
* Thanks to jetbrains for supporting this open source project
* [OpenSourceSupport](https://jb.gg/OpenSourceSupport)
![jetbrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png?_ga=2.159595956.84150952.1649035676-1273448.1647342519&_gl=1*1v0d1hp*_ga*MTI3MzQ0OC4xNjQ3MzQyNTE5*_ga_V0XZL7QHEB*MTY0OTAzNTY3NS4xLjEuMTY0OTAzODA2Ni42MA..)
