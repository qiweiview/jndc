![J NDC](https://s1.ax1x.com/2020/11/04/B6HETJ.png)
![jdk12](https://img.shields.io/badge/jdk-8-orange.svg)


## 文档摘要
* [常见问题](https://github.com/qiweiview/jndc/blob/master/QA.md)
* [项目介绍](#项目介绍)
* [项目使用范例](#项目使用范例)
* [协议说明](#协议说明)
* [功能说明](#功能说明)
* [配置文件说明](#配置文件说明)
* [小结](#小结)



## 项目介绍
* "J NDC" 是 "java no distance connection"的缩写，意在提供简便易用的端口映射应用，应用基于netty编写。
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

## 功能说明
* [功能介绍](https://github.com/qiweiview/jndc/blob/master/function_introduction.md)

## 配置文件说明

### server 配置
```yaml
secrete: "jndc" # server端密钥，client端配置需要持有相同字符串。很重要务必在使用前更改,不允许默认密码为‘jndc’运行
loglevel: "info" # 日志打印等级
blackList: # ip访问黑名单，ip限制覆盖除管理端口（即managementApiPort）外的所有端口监听
  #- "192.168.1.1"
whiteList: # ip访问白名单
  #- "192.168.1.2"
loginName: "jndc" # 内部管理api登录所需用户名，不允许默认值‘jndc’
loginPassWord: "jndc" # 内部管理api登录所需密码，不允许默认值‘jndc’
useSsl: false
keyStoreFile: '/home/xxx.cn.jks' # 证书文件，仅支持jks格式，校验失败控制台会提示
keystorePass: 'xxx' # jks文件密钥，校验失败控制台会提示
managementApiPort: "443" #管理api端口
adminPort: "81" # jndc建立隧道端口，端口用于支持ndc协议调用
bindIp: "0.0.0.0" # jndc服务端运行ip
```

### client 配置
```yaml
secrete: "xxx" # client端配置需要持有相同字符串。很重要务必在使用前更改,不允许默认密码为‘jndc’运行
loglevel: "info" # 日志打印等级
serverIp: "127.0.0.1" # 服务端运行监听ip
serverPort: "81" # 服务端运行端口
openGui: true
autoReleaseTimeOut: 1200000 # 客户端连接自动断开时间（毫秒）
clientServiceDescriptions: # 注册服务
  - serviceName: "desk" # 服务名称(仅作为注册的标识)
    serviceIp: "192.168.216.131" #  服务所在网络ip
    servicePort: "3389" #  服务监听端口
    serviceEnable: true # 该服务是否在客户端启动后自动注册，即:'true'则会在客户端启动后自动将该服务注册到服务端,反之
  - serviceName: "echo"
    serviceIp: "127.0.0.1"
    servicePort: "888"
    serviceEnable: false

```

## 小结
* 学识尚浅，功能可能存在“重复造轮子”问题
* 如若有好的功能需求，或代码存在的bug欢迎在issue里提出

## 开发计划
* 流量监控



