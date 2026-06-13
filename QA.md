# 常见问题

##  Q：java.lang.NoClassDefFoundError: javafx/application/Application 无法加载类
* jdk11 后将javafx独立成一个模块
* 解决方法一：单独引入javafx
```
<!-- https://mvnrepository.com/artifact/org.openjfx/javafx -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx</artifactId>
    <version>11</version>
    <type>pom</type>
</dependency>

```
* 当前项目构建和运行要求 JDK 21。如确实需要 JavaFX，请在 JDK 21 环境下单独引入 OpenJFX 依赖或 SDK。

## Q：为什么协议直接定死了IPV4，不能兼容IPV6吗
* 作者认为穿透根本是解决IPV4地址不足造成的访问不可到达问题，若使用IPV6则不存在以上问题。
