![J NDC](https://s1.ax1x.com/2020/11/04/B6HETJ.png)
![jdk12](https://img.shields.io/badge/jdk-8-orange.svg)


## 文档摘要
* [常见问题](https://github.com/qiweiview/jndc/blob/master/QA.md)
* [项目介绍](#项目介绍)
* [项目目录结构](#项目目录结构)
* [项目使用范例](#项目使用范例)
* [数据流向](#数据流向)
* [ndc协议](#ndc协议)
* [数据加解密](#数据加解密)
* [IP黑白名单](#IP黑白名单)
* [管理API及页面SSL支持](#管理API及页面SSL支持)
* [Client可视化](#Client可视化)
* [配置文件说明](#配置文件说明)
* [小结](#小结)



## 项目介绍
* "J NDC" 是 "java no distance connection"的缩写，意在提供简便易用的端口映射应用，应用基于netty编写。
* 应用以Client/Server架构运行,由本地client端向server端注册本地服务，由server端选择暴露对应服务 
* 应用核心由ndc私有协议支撑，提供了"传输数据加密","ip黑白名单","客户端可视化"功能


## 项目目录结构
```
- jndc
 - jndc_core # 核心公共实现
 - jndc_server # 服务端实现
 - jndc_client # 客户端实现
```

## 项目使用范例
* [范例](https://github.com/qiweiview/jndc/blob/master/tutorial.md)

## 数据流向
```
broser     ------->               (tunnel)               ---------->local_app
client     -------> jndc server <----------> jndc client ---------->local_app
other      ------->                                      ---------->local_app
```

## ndc协议
* 协议设计为仅支持ipv4
* 单包数据长度限制,超出将自动拆包
```
public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024
```

* 协议结构：
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

## 数据加解密
* 应用通过```DataEncryption```接口对协议内"变长部分数据”进行加解密，应用默认使用AES算法执行加解密过程。
* 可替换为非对称加密或更为安全的加密算法

## IP黑白名单
* IP名单限制覆盖：
 1. server端隧道端口（非名单规则内ip无法通过client向server注册服务）
 2. server端暴露出的服务端口(非名单规则内ip无法访问暴露的服务)
 
* 可通过配置文件限制服务端IP黑白名单：
1. 黑名单：名单内ip禁止访问
2. 白名单：仅白名单内ip放行 
* 默认白名单拥有更高权重，即黑白名单均出现127.0.0.1,那么127.0.0.1将被放行
```yaml
blackList:
  - "127.0.0.1"
  - "192.168.1.1"

whiteList:
  - "127.0.0.1"
  - "127.0.0.2"
```

## 管理API及页面SSL支持
* 服务端支持对管理api配置ssl证书,目前只支持jks方式
* 配置服务端配置文件参数,'useSsl'为true时会使用'keyStoreFile'处配置的证书
```
  useSsl: true
  keyStoreFile: 'C:\Users\xxx\Desktop\xxx.cn\Tomcat\xxx.cn.jks'
  keystorePass: 'xxx'
```

## Client可视化
* 客户端开启可视化配置
```yaml
openGui: true
```
* 启动后将打开客户端
* [![r1y3PH.png](https://s3.ax1x.com/2020/12/16/r1y3PH.png)](https://imgchr.com/i/r1y3PH)
* 支持动态 开启/关闭/删除 服务
* [![r1yNsP.png](https://s3.ax1x.com/2020/12/16/r1yNsP.png)](https://imgchr.com/i/r1yNsP)

## 配置文件说明

### server 配置
```yaml
secrete: "jndc" # server端密钥，client端配置需要持有相同字符串。很重要务必在使用前更改,不允许默认密码为‘jndc’运行
loglevel: "info" # 日志打印等级
blackList: # ip访问黑名单，ip限制覆盖除管理端口（即managementApiPort）外的所有端口监听
  #- "192.168.1.1"
whiteList: # ip访问白名单
  #  - "192.168.1.2"
frontProjectPath: '/usr/local/default_management/' # 前端管理项目地址,若‘deployFrontProject’参数为true，则该地址文件夹将被作为静态项目部署到‘managementApiPort’端口
deployFrontProject: true # 是否部署‘frontProjectPath’参数地址下的项目到‘managementApiPort’端口,false不会部署，反之
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
* 业务级IP记录
* 通道巡检


