 ### 介绍  
 jvmdog是用来辅助在线分析java代码运行的工具，提供动态修改java代码，监控和修改对象实例，以及动态运行指定代码的功能；  
 整个上分为：
 - client：客户端，运行在需要监控的主机上；主要是将agent注入到目标jvm进程；
 - agent：以java-agent方式注入到目标jvm进程，直接与server通信以便接受并执行server发送的指令；
 - server：client和agent连接到server，server直接发送指令到agent；
 
### 运行
 1. 下载发布版本：https://github.com/chunsen/jvmdog/releases/tag/v0.0.1-rc1  
 2. 运行server：  
    进入server目录，以java -jar的方式启动  
    java -jar jvmdog-server-0.0.1-SNAPSHOT-all.jar  
    server是一个spring boot的web应用，可根据需要调整application.properties的配置项；  
3.  运行client：  
    将client目录复制到目标主机上，通过如下命令启动：   
     java -Xbootclasspath/a:$JAVA_HOME/lib/tools.jar -jar jvmdog-client-0.0.1-SNAPSHOT-all.jar  
    如果需要监控多台机器，可以将client分别复制到对应机器上后启动client即可；  
4.  访问server：http://<server>:8181/

### 命令介绍
#### client命令
- attach： 将agent attach到目标进程，attach之后就可以执行agent命令
- detach： 将agent从目标进程detach，detach之后不可用执行agent命令

#### agent命令
- 查询实例：根据类名查询进程中存在的实例
- 对象查询：使用SQL查询进程中的对象实例；
- 对象更新：使用SQL更新进程中对象的属性；
- 监控新对象：监控方法执行过程中新产生的对象；
- 堆栈信息：查询存在类pattern的线程栈；
- 运行代码：在目标进程中运行指定的代码；

### 测试应用
可以使用jvmdog-example作为目标应用进行测试：https://github.com/chunsen/jvmdog-example   