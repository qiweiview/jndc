# 功能列表


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
