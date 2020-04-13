# snowStormRpc
此项目为一个基于java语言的rpc框架，满足了基本的跨服务调用，以及服务的高可用。
项目内部采用了Netty4作为基础框架以及kryo序列化框架。
服务分为3个角色包括dispatcher（连接分发器和负载均衡，以及router集群的注册中心），caller（服务提供者，和消费者），router（服务调用路由）。
此外项目中还包含了与springboot整合的工程。客户端代码集成此项目后可基于注解进行开发，并将自己的项目变为caller。
目前处于初级阶段，项目已经能跑起来，效果也可实现。后面还需要对性能，日志项目结构进行调整
  
  

用法：  
将项目安装到maven本地仓库后按照以下执行：    

1.snowstorm-dispatcher：  
找到程序入口DispatcherStarter，在dispatcher.properties中配置好服务运行端口，运行main  
2.snowstorm-router：
找到RouterStarter类，此为程序的入口，在router.properties配置好服务端口，以及上面dispatcher的地址，运行main，可以集群部署。修改端口号另外启动一个main即可  
3.snowstorm-client:  
配合springbootstarter整合包使用

在自己的项目中引入
               

		<dependency>
			<groupId>com.hzc.common</groupId>
			<artifactId>snowstorm-client</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
                
                <dependency>
			<groupId>com.hzc.rpc</groupId>
			<artifactId>spring-boot-starter-snowstorm</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>


配置文件：
```
spring:
  application:
    name: testApp2 #配置服务名

snowstorm:  
  dispatcher:  
    addr: localhost:8090 #配置dispatcher地址  
  queueNumbers: 3 #配置rpc请求队列数（）  
  queueCapacity: 20 #配置队列的大小  
  maxThread: 4 #配置最大线程数，一定要大于等于queueNumbers 
```  
  
  
  1.在springboot项目*testApp*（即配置文件中的服务名）中
  定义一个caller:用@SnowCaller标记为一个调用接口。  
  @MethodId指明 *methodId*即方法的唯一id，可以理解为http的url一样，*callerServerName*即为调用的哪个服务，它的值为要调用的服务的配置文件中的配置的服务名如：*testApp2*服务  
```
@SnowCaller
public interface Caller {

    @MethodId(methodId = "xxx", callServerName = "testApp2")
    public String xxx();

}
```  





2.在springboot项目*testApp2*中定义一个*Provider* 用@MethodId的methodId标识方法的唯一id（此处与caller的methodId对应）
```  
@SnowProvider
public class Provider {
    @Autowired
    private TestServiceSnow testServiceSnow;

    @MethodId(methodId = "xxx")
    public String xxx(){
        return testServiceSnow.xxSnow();
    }
}
```

3.这两个项目中在配置类中加入配置注解  callerPackage为caller的包位置，providerPackage为Provider的包位置
```  
@Configuration
@SnowStormScanner(callerPackage = "com.desinthinking.snowstorm",
        providerPackage = "com.desinthinking.snowstorm")
public class ConfigSnow {
}
```  


 4.在应用程序中通过@Autowired等注入方式把caller注入到需要使用的类中，直接调用即可。

# v0.0.2目标
1.优化线程模型  
2.消息的消费队列还有一些bug  
3.日志打印现在直接在控制台打印，后面引入log4j  
4.dispatcher集群支持  
5.线程安全性优化，效率优化  
6.starter包中加入methodId自动生成
7.修复bug
。。。。。。。。



