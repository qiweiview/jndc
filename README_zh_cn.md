![J NDC](https://s1.ax1x.com/2020/11/04/B6HETJ.png)
![jdk12](https://img.shields.io/badge/jdk-8-orange.svg)


## [English Document](https://github.com/qiweiview/jndc/blob/master/README.md)



## 项目介绍
* "J NDC" 是 "java no distance connection"的缩写，意在提供简便易用的可视化内网穿透应用，应用基于java netty编写。
* 应用以Client/Server架构构建,通过"服务注册"思路，由本地client端向server端提供本地服务，由server端管理暴露对应服务 
* 应用核心由ndc私有协议支撑，提供了"传输数据加密","ip黑白名单","客户端可视化","服务端口定时","域名路由"功能
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
blackList: # ip访问黑名单
#- "192.168.1.1"
whiteList: # 白名单
#- "192.168.1.2"
servicePort: 81 # jndc服务端运行监听端口
bindIp: "127.0.0.1" # jndc服务端运行ip

dbConfig:
  type: "mysql" # 可选值：mysql和sqlite
  #  type: "sqlite" # 可选值：mysql和sqlite
  url: "jdbc:mysql://127.0.0.1:3306/jndc?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true"
  name: "root"
  password: "xxx"

manageConfig: # 管理端api服务
  managementApiPort: 777 #管理api端口
  useSsl: false # 是否使用ssl
  jksPath: "/xx.jks" #jks 证书地址
  jksPass: "xxx" # jks 证书密码
  loginName: "xxx" # 登录用户名
  loginPassWord: "xxx" # 登录密码
  adminEnable: true # 是否启动静态页面

webConfig: # http web服务
  notFoundPage: "/404.html"
  httpPort: 80 # http应用端口
  useSsl: false # 是否使用ssl
  jksPath: "/xx.jks" #jks 证书地址
  jksPass: "ddd" # jks 证书密码
```

### client 配置
```yaml
secrete: "xxx1" # 服务端密钥，很重要务必在使用前更改
loglevel: "info" # 日志打印等级
serverIp: "127.0.0.1" # 服务端运行监听ip
serverPort: "81" # 服务端运行端口
openGui: false
autoReleaseTimeOut: 600000 # 客户端自动断开时间（毫秒）
clientServiceDescriptions: # 注册服务
  - serviceName: "xx"
    serviceIp: "xx.com"
    servicePort: "80"
    serviceEnable: true
```

## 小结
* 如若有好的功能需求，或代码存在的bug欢迎在issue里提出

## 开发计划
* http证书配置支持
* 流量监控
* 查询语法优化

## supporting
* Thanks to jetbrains for supporting this open source project
* [OpenSourceSupport](https://jb.gg/OpenSourceSupport)
![jetbrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png?_ga=2.159595956.84150952.1649035676-1273448.1647342519&_gl=1*1v0d1hp*_ga*MTI3MzQ0OC4xNjQ3MzQyNTE5*_ga_V0XZL7QHEB*MTY0OTAzNTY3NS4xLjEuMTY0OTAzODA2Ni42MA..)



