![J NDC](https://s1.ax1x.com/2020/11/04/B6HETJ.png)
![jdk12](https://img.shields.io/badge/jdk-8-orange.svg)


## Documentation summary|[中文文档](https://github.com/qiweiview/jndc/blob/master/README_zh_cn.md)
* [FAQ](https://github.com/qiweiview/jndc/blob/master/QA.md)
* [Project introduction](#project introduction)
* [Project usage example](#Project usage example)
* [protocol description](#protocol description)
* [Function description](#Function description)
* [Configuration file description](#Configuration file description)
* [Summary](#Summary)



## Project Introduction
* "J NDC" is the abbreviation of "java no distance connection", which is intended to provide an easy-to-use visual intranet penetration application. The application is written based on java netty.
* The application is built with the Client/Server architecture. Through the idea of "service registration", the local client side provides local services to the server side, and the server side manages and exposes the corresponding services
* The application core is supported by the ndc private protocol, providing "transmission data encryption", "ip black and white list", "client visualization", "service port timing", "domain name routing" functions
* Project source code directory structure
```
- jndc
  - jndc_core # core public implementation
  - jndc_server # server implementation
  - jndc_client # client implementation
```

* TCP data flow
```
broser     ------->               (tunnel)               ---------->local_app
client     -------> jndc server <----------> jndc client ---------->local_app
other      ------->                                      ---------->local_app
```

## Project usage example
* [Example](https://github.com/qiweiview/jndc/blob/master/tutorial.md)


## Protocol description
* NDC protocol
* The protocol is designed to only support ipv4
* The data length limit of a single packet, beyond which will be automatically unpacked
```
public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024
```

* Agreement description:
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
## Configuration file description

### server configuration
````yaml
secrete: "xxx" # Server secret, very important, be sure to change it before use
loglevel: "info"
blackList: # ip access blacklist
#- "192.168.1.1"
whiteList: # whitelist
#- "192.168.1.2"
servicePort: 81 # jndc server running listening port
bindIp: "127.0.0.1" # jndc server running ip

dbConfig:
  type: "mysql" # optional values: mysql and sqlite
  # type: "sqlite" # optional values: mysql and sqlite
  url: "jdbc:mysql://127.0.0.1:3306/jndc?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true"
  name: "root"
  password: "xxx"

manageConfig: # management-side api service
  managementApiPort: 777 #Management api port
  useSsl: false # Whether to use ssl
  jksPath: "/xx.jks" #jks certificate address
  jksPass: "xxx" # jks certificate password
  loginName: "xxx" # login user name
  loginPassWord: "xxx" # login password
  adminEnable: true # Whether to start a static page

webConfig: # http web service
  notFoundPage: "/404.html"
  httpPort: 80 # http application port
  useSsl: false # Whether to use ssl
  jksPath: "/xx.jks" #jks certificate address
  jksPass: "ddd" # jks certificate password
````

### client configuration
````yaml
secrete: "xxx1" # Server secret, it is very important to change it before use
loglevel: "info" # log print level
serverIp: "127.0.0.1" # Server running listening ip
serverPort: "81" # server running port
openGui: false
autoReleaseTimeOut: 600000 # Client auto disconnect time (milliseconds)
clientServiceDescriptions: # register service
  - serviceName: "xx"
    serviceIp: "xx.com"
    servicePort: "80"
    serviceEnable: true
````

## Summary
* If you have good functional requirements, or bugs in the code, please submit them in the issue

## Development Plan
* http certificate configuration support
* data monitoring
* Query syntax optimization

## supporting
* Thanks to jetbrains for supporting this open source project
* [OpenSourceSupport](https://jb.gg/OpenSourceSupport)
![jetbrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png?_ga=2.159595956.84150952.1649035676-1273448.1647342519&_gl=1*1v0d1hp*_ga*MTI3MzQ0OC4xNjQ3MzQyNTE5*_ga_V0XZL7QHEB*MTY0OTAzNTY3NS4xLjEuMTY0OTAzODA2Ni42MA..)



