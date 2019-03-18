
# 修改git
+ git commit -m "Change repo." # 先把所有为保存的修改打包为一个commit
+ git remote remove origin # 删掉原来git源
+ git remote add origin [YOUR NEW .GIT URL](https://github.com/lijianl/netty-learn-note.git) # 将新源地址写入本地版本库配置文件
+ git push -u origin master # 提交所有代码

# 分支
+ http-test  源代码分支
+ master - 自己修改
+ netty-x - 大牛的分支

### http-async-test 
> average response time = 4859, tps = 50 (同步)
> ART = ,TPS = (异步)
  
    + undertow | tomcat | jetty线程的模型/IO模型
    +
    ```
       <dependency>
           <groupId>org.asynchttpclient</groupId>
           <artifactId>async-http-client</artifactId>
           <version>2.4.7</version>
       </dependency>
    ```
    
# 环境搭建


# 总结

### demo解决的问题
1. 服务注册
2. 服务发现
3. 服务负载
4. 服务通信 -> 高性能

### 技术分析
> reactor模型，负载均衡，线程，锁，io通信，阻塞与非阻塞，零拷贝，序列化，http/tcp/udp与自定义协议，批处理，垃圾回收，服务注册发现

    