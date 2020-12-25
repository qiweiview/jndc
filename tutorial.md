# 项目使用范例

## 范例一
### 暴露内网192.168.216.31(举例)设备上的 "nginx服务" 和 "远程桌面服务" 到公网服务设备118.29.31.43(举例)的任意端口上
* 安装jdk 1.8+
* [下载应用](https://github.com/qiweiview/jndc/releases/tag/v20201216) 
```yaml
release20201216_client.zip # 客户端压缩包
release20201216_server.zip # 服务端压缩包
release20201216_front_management.zip # 前端项目压缩包
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
  - (f)jndc_server-1.0.jar # server端java程序
  - (f)server_start.bat # window server 启动脚本
  - (f)server_start.sh # linux server 启动脚本
  - (f)config.yml # server 配置文件
```
#### 部署server端
* 将linux_server文件夹拷贝至公网服务器目录/usr/local/jndc(举例),
* 启动前需要修改config.yml文件
```yaml
secrete: "scdfat!`" # 尽量复杂，不支持初始密码‘jndc’
loglevel: "info"
blackList: # ip访问黑名单
# - "192.168.1.1"
whiteList: # 白名单
#  - "192.168.1.2"
frontProjectPath: '/usr/local/jndc/default_management/' #管理端项目地址(release中有发布对应前端项目)
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

