![J NDC](https://s1.ax1x.com/2020/11/04/B6HETJ.png)
![jdk12](https://img.shields.io/badge/jdk-8-orange.svg)


## 文档摘要
* [介绍](#介绍)
* [使用范例](#使用范例)
* [数据流向](#数据流向)
* [ndc协议](#ndc协议)
* [数据加解密](#数据加解密)
* [IP黑白名单](#IP黑白名单)
* [管理API及页面SSL支持](#管理API及页面SSL支持)
* [配置文件说明](#配置文件说明)
* [小结](#小结)



## 介绍
* "J NDC" 是 "java no distance connection"的缩写，意在提供简便易用的端口映射应用，应用基于netty编写。
* 应用以Client/Server架构运行,由本地client端向server端注册本地服务，由server端选择暴露对应服务 
* 应用核心由ndc私有协议支撑，提供了"传输数据加密","ip黑白名单"功能

## 使用范例
### 范例一 暴露内网192.168.216.31(举例)设备上的nginx和远程桌面服务器到公网服务器118.29.31.43(举例)的任意端口上
* 安装jdk 1.8+
* [下载合适的版本](https://github.com/qiweiview/jndc/releases/) (java语言跨平台特性，实际两个版本仅启动脚本差异)
* 解压后文件目录
```yaml
# (f)开头为文件 (d)开头为文件夹

- linux_client
  - (f)client_start.sh #client启动脚本
  - (f)client_stop.sh # client停止脚本
  - (f)config.yml # client配置文件
  - (f)jndc-1.0.jar # client java 应用
  
- linux_server
  - (f)server_start.sh # server 启动脚本
  - (f)server_stop.sh # server 停止脚本
  - (f)config.yml # server 配置文件
  - (d)default_management  # server 管理界面前端项目
```
#### 部署server端
* 将linux_server文件夹拷贝至公网服务器目录/usr/local/jndc(举例),
* 启动前需要修改config.yml文件
```yaml
# ------------------------------------------------------general config line------------------------------------------------------
# general config
secrete: "scdfat!`" # 尽量复杂，不支持初始密码‘jndc’
loglevel: "info"

# ------------------------------------------------------server config line------------------------------------------------------

# server配置
serverConfig:
  blackList: # ip访问黑名单
  # - "192.168.1.1"
  whiteList: # 白名单
  #  - "192.168.1.2"
  frontProjectPath: '/usr/local/jndc/default_management/' #管理端项目地址,对应举例中/usr/local/jndc
  deployFrontProject: true # 是否扫描部署前端项目（管理端）,false则不会启动前端项目，但web接口仍旧会启动
  loginName: "test33" # 管理界面用户名
  loginPassWord: "test_pass1`" # 管理界面密码
  managementApiPort: "82" # 管理界面访问端口
  adminPort: "81" # jndc服务端运行监听端口
  bindIp: "118.29.31.43" # jndc服务端运行ip
```

* 运行server_start.sh脚本,日志将被输出至同级log目录下
```yaml
# 控制台返回
start jndc success
```


#### 部署client端
* 将linux_client文件加拷贝至内网服务器192.168.216.31
* 启动前需要修改config.yml文件
```yaml
# ------------------------------------------------------general config line------------------------------------------------------
# 通用配置
secrete: "scdfat!`" # 和上面服务端内要一直
loglevel: "info" # 日志打印等级

# ------------------------------------------------------client config line------------------------------------------------------

# client配置
clientConfig:
  serverIp: "118.29.31.43" # 服务端运行监听ip
  serverPort: "81" # server端jndc运行端口
  clientServiceDescriptions: # 要注册的服务
    - serviceName: "nginx服务"  # 服务名称
      serviceIp: "127.0.0.1" # 服务连接ip
      servicePort: "80"  # 服务监听端口
      serviceEnable: true # 服务是否可用    
    - serviceName: "远程桌面"  # 服务名称
      serviceIp: "127.0.0.1" # 服务连接ip
      servicePort: "3389"  # 服务监听端口
      serviceEnable: true # 服务是否可用    
```
* 运行client_start.sh脚本,日志将被输出至同级log目录下


#### 管理暴露已注册服务
* 打开浏览器登录管理页面
```
http://118.29.31.43:82
```
[![Dddok9.png](https://s3.ax1x.com/2020/11/25/Dddok9.png)](https://imgchr.com/i/Dddok9)
* 登录后左侧为主要的几个功能菜单，可以看到管理端已经显示连接到该设备上的客户端列表，并显示了对应注册的服务数
[![DdB0eO.png](https://s3.ax1x.com/2020/11/25/DdB0eO.png)](https://imgchr.com/i/DdB0eO)
* 列表展示了该设备上注册的所有服务
[![DdDkX6.png](https://s3.ax1x.com/2020/11/25/DdDkX6.png)](https://imgchr.com/i/DdDkX6)
* 添加一个***非常用端口监听***,并选择在该端口上暴露的服务
[![DdD4jx.png](https://s3.ax1x.com/2020/11/25/DdD4jx.png)](https://imgchr.com/i/DdD4jx)

* ***！！！最重要一步！！！*** 设置常用设备出口ip至白名单，限制仅常用设备ip可访问
[![Dds9Q1.png](https://s3.ax1x.com/2020/11/25/Dds9Q1.png)](https://imgchr.com/i/Dds9Q1)

* 访问暴露的服务端口
```yaml
http://118.29.31.43:8077
```



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

## 配置文件说明

### server 配置
```yaml
# ------------------------------------------------------general config line------------------------------------------------------
# general config
secrete: "jndc" # server端密钥，client端配置需要持有相同字符串。很重要务必在使用前更改,不允许默认密码为‘jndc’运行
loglevel: "info" #日志打印等级


# ------------------------------------------------------server config line------------------------------------------------------

# 服务端配置
serverConfig:
  blackList: # ip访问黑名单，ip限制覆盖除管理端口（即managementApiPort）外的所有端口监听
  # - "192.168.1.1"
  whiteList: # ip访问白名单
  # - "192.168.1.2"
  frontProjectPath: '/usr/local/default_management/' #前端管理项目地址,若‘deployFrontProject’参数为true，则该地址文件夹将被作为静态项目部署到‘managementApiPort’端口
  deployFrontProject: true # 是否部署‘frontProjectPath’参数地址下的项目到‘managementApiPort’端口,false不会部署，反之
  loginName: "jndc" # 内部管理api登录所需用户名，不允许默认值‘jndc’
  loginPassWord: "jndc" # 内部管理api登录所需密码，不允许默认值‘jndc’
  managementApiPort: "82" # 内部管理api监听端口，若‘deployFrontProject’为true,那么‘frontProjectPath’位置的前端项目也将被部署到这个端口上
  useSsl: true # true时，会为’managementApiPort‘端口加载’keyStoreFile‘证书
  keyStoreFile: 'C:\Users\xxx\Desktop\xxx.cn\Tomcat\xxx.cn.jks' # 证书文件，仅支持jks格式，校验失败控制台会提示
  keystorePass: 'xxx' # jks文件密钥，校验失败控制台会提示
  adminPort: "81" # jndc建立隧道端口，端口用于支持ndc协议调用
  bindIp: "0.0.0.0" # jndc服务端运行绑定的网卡ip
```

### client 配置
```yaml
# ------------------------------------------------------general config line------------------------------------------------------
# 通用配置
secrete: "jndc" #server端密钥，client端配置需要持有相同字符串。很重要务必在使用前更改,不允许默认密码为‘jndc’运行
loglevel: "info" # 日志打印等级

# ------------------------------------------------------client config line------------------------------------------------------

# 客户端配置
clientConfig:
  serverIp: "192.168.1.1" # 服务端运行监听ip
  serverPort: "81" # 服务端运行端口
  clientServiceDescriptions: # 注册服务
    - serviceName: "nginx"  # 服务名称(仅命名，无其他作用)
      serviceIp: "192.168.2.1" # 本地服务ip
      servicePort: "80"  # 本地服务端口
      serviceEnable: true # 服务是否开启，设置为false则该配置将被跳过，不被读取使用    
```

## 小结
* 学识尚浅，功能可能存在“重复造轮子”问题
* 如若有好的功能需求，或代码存在的bug欢迎在issue里提出

