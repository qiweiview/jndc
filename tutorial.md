# 项目使用范例

## 范例一
### 暴露内网192.168.216.31(举例)设备上的 "nginx服务" 和 "远程桌面服务" 到公网服务设备118.29.31.43(举例)的任意端口上
* 安装jdk 1.8+
* [下载应用](https://github.com/qiweiview/jndc/releases) 
```yaml
release20201216_client.zip # 客户端压缩包
release20201216_server.zip # 服务端压缩包
```
* 文件目录结构
```yaml
# (f)开头为文件 (d)开头为文件夹

- release20201216_client
  - (f)jndc_server-1.0.jar # client端java程序
  - (f)server_start.bat # window client 启动脚本
  - (f)server_start.sh # linux client 启动脚本
  - (f)config.yml # client 配置文件
  
- release20201216_server
  - (d)management # 管理端项目
  - (f)jndc_server-1.0.jar # server端java程序
  - (f)server_start.bat # window server 启动脚本
  - (f)server_start.sh # linux server 启动脚本
  - (f)config.yml # server 配置文件
```
#### 部署server端
* 将linux_server文件夹拷贝至公网服务器目录/usr/local/jndc(举例),
* 启动前需要修改config.yml文件
```yaml
secrete: "xxx" # 服务端密钥，非常重要务必在使用前更改
loglevel: "info"
blackList: # ip访问黑名单
#- "192.168.1.1"
whiteList: # 白名单
#- "192.168.1.2"
managementApiPort: 777 #管理api端口
servicePort: 81 # jndc服务端运行监听端口
bindIp: "0.0.0.0" # jndc服务端运行ip
webConfig: # http配置
  routNotFoundPage: ""
  httpPort: 80 # http应用端口
  loginName: "xxx" # 登录用户名
  loginPassWord: "xxx" # 登录密码
  useSsl: false # 是否使用ssl
  scanFrontPages: false # 是否扫描 项目目录下的management文件夹
  jksPath: "" #jks 证书地址
  jksPass: "" # jks 证书密码

```

* 修改前端项目中配置
```
window.runtimeConfig = {
    //举例，若本项目部署于公网服务器123.207.111.212（举例）上
    //则该文件配置参照以下配置
    BASE_REQUEST_PATH : 'http://123.207.111.212:443/',
    BASE_WEBSOCKET_PATH : 'ws://123.207.111.212:443/'

    //若打开ssl证书则需要切换使用下方配置
    //BASE_REQUEST_PATH : 'https://123.207.111.212:443/',
    //BASE_WEBSOCKET_PATH : 'wss://123.207.111.212:443/'
}
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
secrete: "scdfat!`" # 和上面服务端内要一致
loglevel: "info" # 日志打印等级
serverIp: "118.29.31.43" # 服务端运行监听ip
serverPort: "81" # server端jndc运行端口
openGui: false # 'true'时会同时打开客户端可视化界面
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
[![D2pVDx.png](https://s3.ax1x.com/2020/11/30/D2pVDx.png)](https://imgchr.com/i/D2pVDx)

* ***！！！最重要一步！！！*** 设置常用设备出口ip至白名单，限制仅常用设备ip可访问
[![D2pi8J.png](https://s3.ax1x.com/2020/11/30/D2pi8J.png)](https://imgchr.com/i/D2pi8J)

* 访问暴露的服务端口
```yaml
http://118.29.31.43:8077
```

