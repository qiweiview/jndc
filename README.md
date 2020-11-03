# J NDC✈️✈️✈️
![jdk12](https://img.shields.io/badge/jdk-8-orange.svg) 

## 介绍
* "j ndc" 是 "java no distance connection"的缩写，意在提供简便易用的端口映射应用，应用基于netty编写。 
* 应用核心由ndc私有协议支撑

## ndc协议
* 协议设计为仅支持ipv4
* 单包数据长度限制,超出将自动拆包
```
public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024
```
* 协议结构：
```
--------------------------------
  3byte      2byte      1byte
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
            7byte
|          data length         |
--------------------------------
           data length byte
|            data              |
--------------------------------
```

## 数据加解密
* 应用通过```DataEncryption```接口对协议内"变长部分数据”进行加解密，默认使用AES算法执行加解密过程。
* 可替换为更为安全的非对称加密

## 开发计划
* IP黑白名单
* 流量特征识别
* 可视化操作页面
