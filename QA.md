# 常见问题

##  java.lang.NoClassDefFoundError: javafx/application/Application 无法加载类
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
* 解决方法二：降低运行环境jre版本至8-10（本项目使用jdk8执行构建编译） 
