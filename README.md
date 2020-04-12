# snowStormRpc
此项目为一个基于java语言的rpc框架，满足了基本的跨服务调用，以及服务的高可用。
项目内部采用了Netty4作为基础框架以及kryo作为消息编解码器。
服务分为3个角色包括dispatcher（连接分发器和负载均衡，以及router集群的注册中心），caller（服务提供者，和消费者），router（服务调用路由）。
此外项目中还包含了与springboot整合的工程。客户端代码集成此项目后可基于注解进行开发，并将自己的项目变为caller。
目前处于初级阶段，项目已经能跑起来，效果也可实现。后面还需要对性能，日志项目结构进行调整

用法：
安照以下顺序执行：
1.snowstorm-dispatcher：
找到程序入口DispatcherStarter，在dispatcher.properties中配置好服务运行端口，运行main

2.snowstorm-router：
找到RouterStarter类，此为程序的入口，在router.properties配置好服务端口，以及上面dispatcher的地址，运行main






