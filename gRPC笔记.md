# gRPC笔记

## gRPC简介

RPC（Remote Procedure Call，远程过程调用）

由Stubby演化而来

gRPC 核心设计思路：

1. 网络通信 ----> gRPC自己封装网络通信部分，提供多种语言（Java[Netty]）
2. 协议 ----> HTTP2 传输数据的时候 二进制数据。支持双向流（双工）连接的多路复用。
3. 序列化 ----> 基于文本 JSON    基于二进制 protocol buffers
4. 代理的创建 ----> stub存根

**Http2.0协议**

**Http1.0协议**：请求响应的模式 短链接协议**（无状态协议）** HttpSession解决状态问题 传输数据文本结构 单工（只能从客户端找服务端）无法实现服务器端的推送，变相实现推送（客户端轮询）

*http底层是tcp而tcp是长连接，为什么http是短链接？*

http主动断掉链接

*什么是推送？*

当服务端有新的数据，由服务端主动发送到客户端。http1.0使用轮询的方式进行实现，每隔一段时间向服务端发送请求来获取“推送”。但是增加客户端负担

**Http1.1协议**：请求响应的模式 有限的长连接。保持一段时间，`keepalived`	衍生升级成WebSocket方式 双工 实现服务器向客户端推送。

http1.x总结：

1. 传输数据文本格式，可读性好但是效率差
2. 本质上Http1.x无法实现双工通信
3. 资源的请求需要发送多次请求，建立多个连接（HTML,JS,CSS） *如何优化？* 动静分离、CDN

**Http2.0协议**

Http2.0是一个二进制协议，一个请求 一个连接可以请求多个数据【多路复用】

Http2.0三个概念

1. 数据流 stream
2. 消息 message
3. 帧 frame

![image-20231018143425224](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20231018143425224.png)

*队头阻塞&串行请求？？*

相关概念

1. 数据流的优先级
2. 流控，client发送数据太快，服务端处理不了，通知client暂停数据的发送

## Protocol Buffers

protobuf 是一种于编程语言，平台无关的中间语言

protobuf两个版本 proto2 proto3，但是目前主流应用都是3

protobuf需要安装protobuf编译器。编译器的目的可以把protobuf的IDL语言转换成某一种开发语言

### protobuf编译器安装

idea插件安装

### protobuf语法

- 文件格式

  ```markdown
  .proto
  
  UserService.proto
  ```

- 版本设定

  ```protobuf
  syntax = "proto3";
  ```

- 注释

  ```protobuf
  //单行注释
  
  /* 多行注释 */
  ```

- 与java语言相关的语法

  ```markdown
  #后续protobuf生成的java代码是一个源文件还是多个源文件
  option java_multiple_files = false;
  
  #指定protobuf生成的类放在哪个包中
  option java_package = "";
  
  #protobuf生成类的外部类的名字
  option java_outer_classname ="";
  ```

- 逻辑包

  ```markdown
  #protobuf对于文件内容的管理
  package xxx;
  ```

- 导入（多个.proto使用对方的内容）

  ```markdown
  UserService.proto
  
  OrderService.proto
  
  import "xxx/UserService.proto";
  ```

- 基本类型

- 枚举类型

  ```protobuf
  enum SEASON{
  	SPRING = 0;
  	SUMMER = 1;
  	FALL = 2;
  	WINTER =3;
  }
  枚举的值必须从0开始
  ```

- 消息Message

  ```protobuf
  message LoginRequest {
  	string username = 1; //1表示这个字段在message中的编号
  	string password = 2;
  	int32 age = 3;
  }
  // 编号最大2^29-1 其中 19000-19999不能用，protobuf保留
  singular : 这个字段的值只能是0个或1个
  repeated : 这个值的返回值是多个，等价于List
  
  oneof
  message SimpleMessage{
  	oneof test_oneof{
  	string name = 1;
  	int32 age =2;
  	}
  }
  ```

- 服务

  ```protobuf
  service HelloService{
  	rpc hello(HelloRequest) returnsS(HelloResponse){}
  }
  ```

## 第一个gRPC的开发

### 项目结构

```markdown
1.xxx-api 模块
	定义protobuf idl语言
	并且通过命令创建具体的代码，后续client server引入使用。
	1.message
	2.service
2.xxx-server 模块
	1.实现api模块中定义的服务接口
	2.发布gRPC服务（创建服务端程序）
3.xxx-client 模块
	1.创建服务端代理
	2.基于代理的RPC调用
```

### api模块

```markdown
1. .proto文件 写protobuf的IDL
2. protoc命令把proto文件中的IDL转换成编程语言
protoc --java_out=/xxx/xxx /xxx/xxx/xxx.proto
3. 工程中，用maven插件进行编译
```

插件依赖(proto to java)

```xml
<dependency>
  <groupId>io.grpc</groupId>
  <artifactId>grpc-netty-shaded</artifactId>
  <version>1.58.0</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.grpc</groupId>
  <artifactId>grpc-protobuf</artifactId>
  <version>1.58.0</version>
</dependency>
<dependency>
  <groupId>io.grpc</groupId>
  <artifactId>grpc-stub</artifactId>
  <version>1.58.0</version>
</dependency>
<dependency> <!-- necessary for Java 9+ -->
  <groupId>org.apache.tomcat</groupId>
  <artifactId>annotations-api</artifactId>
  <version>6.0.53</version>
  <scope>provided</scope>
</dependency>
```

```xml
<build>
  <extensions>
    <extension>
      <groupId>kr.motd.maven</groupId>
      <artifactId>os-maven-plugin</artifactId>
      <version>1.7.1</version>
    </extension>
  </extensions>
  <plugins>
    <plugin>
      <groupId>org.xolstice.maven.plugins</groupId>
      <artifactId>protobuf-maven-plugin</artifactId>
      <version>0.6.1</version>
      <configuration>
        <protocArtifact>com.google.protobuf:protoc:3.24.0:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.58.0:exe:${os.detected.classifier}</pluginArtifact>
        <!-- 设置输出路径-->
        <outputDirectory>${basedir}/src/main/java</outputDirectory>
        <clearOutputDirectory>false</clearOutputDirectory>
      </configuration>
      <executions>
        <execution>
          <goals>
            <goal>compile</goal>
            <goal>compile-custom</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

![image-20231018194845678](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20231018194845678.png)

![image-20231018201126826](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20231018201126826.png)

```markdown
HelloServiceImplBase 对应真正的服务接口，开发时继承这个类并覆盖其中的方法

stub结尾的类型对应的时client的代理对象，区别是网络通信方式的不同
```

### xxx-server服务端模块

```markdown
1. 实现业务接口 添加具体的功能
2. 创建服务端（Netty）
```

```java
//业务接口
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    /**
     * 1. 接受client提交的参数
     * 2. 业务处理service+dao
     * 3. 提供返回值
    */
    @Override
    public void hello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        //获取提交的参数
        String name = request.getName();
        //业务处理
        System.out.println(name);
        //提供返回值 封装响应
        // 1.创建响应对象的构造者
        // 2.填充数据
        // 3.封装响应
        HelloProto.HelloResponse.Builder builder = HelloProto.HelloResponse.newBuilder();
        builder.setResult("hello method invoke ok");
        HelloProto.HelloResponse helloResponse = builder.build();

        responseObserver.onNext(helloResponse);
        responseObserver.onCompleted();

    }
}
```

```java
//服务端
public class GrpcServer1{
    public static void main(String[] args) throws IOException, InterruptedException {
        //1.绑定端口
        ServerBuilder serverBuilder = ServerBuilder.forPort(9000);

        //2.发布服务
        serverBuilder.addService(new HelloServiceImpl());

        //3.创建服务对象
        Server server = serverBuilder.build();

        server.start();
        server.awaitTermination();
    }
}
```

### xxx-client 模块

```markdown
1. client通过代理对象完成远端对象的调用
```

```java
public class GrpcClient1 {
    public static void main(String[] args) {
        //1.创建通信管道
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        try{
            //2.获取stub 代理对象
            HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
            //3.完成RPC调用
            //准备参数
            HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
            builder.setName("yong");
            HelloProto.HelloRequest helloRequest = builder.build();
            //rpc调用，获取response
            HelloProto.HelloResponse helloResponse = helloService.hello(helloRequest);
            String result = helloResponse.getResult();
            System.out.println(result);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            managedChannel.shutdown();
        }
    }
}
```



## gRPC的四种通信方式

四种通信方式

1. 简单rpc 一元rpc（Unary RPC）
2. 服务流式rpc （Server Streaming RPC）
3. 客户端流式rpc （Client Streaming RPC）
4. 双向流rpc （Bi-directional Stream RPC）

### 简单RPC

第一个grpc程序（上一节）

特点：客户端阻塞等待服务端响应，开发过程中主要采用这种通信方式

在 `.proto`中

```protobuf
service HelloService{
	rpc hello(HelloRequest) returns (HelloResponse){}
}
```

### 服务端流式rpc

特点：一个请求对象，服务端可以回传多个结果对象。不同时刻返回多个结果。

使用场景：炒股软件，client发送股票编号，server返回不同时刻的股票行情。

语法：

```protobuf
service HelloService{
	rpc hello(HelloRequest) returns (stream HelloResponse){}
}
```

```java
//服务端
public void c2ss(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        String name = request.getName();
        System.out.println(name);
        //流式响应
        for(int i = 0;i<10;i++){
            HelloProto.HelloResponse.Builder builder = HelloProto.HelloResponse.newBuilder();
            builder.setResult(""+i);
            HelloProto.HelloResponse helloResponse = builder.build();
            responseObserver.onNext(helloResponse);

            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

//客户端
Iterator<HelloProto.HelloResponse> helloResponseIterator = helloService.c2ss(helloRequest);
            while(helloResponseIterator.hasNext()){
                HelloProto.HelloResponse helloResponse = helloResponseIterator.next();
                System.out.println(helloResponse);
            }
```

**阻塞stub，需要服务端把所有数据传完才开始之后的流程**

**监听异步方式：**

api和服务端没有变化

客户端：

responseObserver监控：

1. onNext()
2. onError()
3. onCompleted()

```java
public class GrpcClient3 {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        try{
            HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);

            HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
            builder.setName("client3");
            HelloProto.HelloRequest helloRequest = builder.build();

            List<HelloProto.HelloResponse> list = new ArrayList<>();
            helloService.c2ss(helloRequest, new StreamObserver<HelloProto.HelloResponse>() {
                @Override
                public void onNext(HelloProto.HelloResponse helloResponse) {
                    System.out.println(helloResponse.getResult());
                    list.add(helloResponse);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    System.out.println(list);
                    System.out.println("end");
                }
            });
            managedChannel.awaitTermination(12,TimeUnit.SECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            managedChannel.shutdown();
            System.out.println("shut dowm");
        }
    }
}
```

**异步编程**因为没有阻塞 顺序执行了代码，服务端响应较慢，客户端执行太快，直接结束了，在shutdown之前加入

```java
managedChannel.awaitTermination(12 , TimeUnit.SECONDS);
```



### 客户端流式rpc

```protobuf
rpc cs2ss(stream HelloRequest) returns (stream HelloResponse){}
```

```java
/**
	客户端发送request时使用observer，即requestObserver
*/
public class GrpcClient4 {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost" ,9000).usePlaintext().build();

        try {
            HelloServiceGrpc.HelloServiceStub helloService  = HelloServiceGrpc.newStub(managedChannel);
            //这里cs2s返回的是observer，我们就使用这个observer来发送请求，再通过cs2s的参数observer来接受服务端发来的响应
            StreamObserver<HelloProto.HelloRequest> helloRequestStreamObserver = helloService.cs2s(new StreamObserver<HelloProto.HelloResponse>() {
                @Override
                public void onNext(HelloProto.HelloResponse helloResponse) {
                    System.out.println(helloResponse.getResult());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    System.out.println("cs2s onCompleted");
                }
            });

            for(int i = 0;i< 10;i++){
                helloRequestStreamObserver.onNext(HelloProto.HelloRequest.newBuilder().setName("client send "+i).build());
                Thread.sleep(1000);
            }
            helloRequestStreamObserver.onCompleted();
            managedChannel.awaitTermination(12 , TimeUnit.SECONDS);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            managedChannel.shutdown();
        }
    }
}

/**
	服务端接受
*/
 public StreamObserver<HelloProto.HelloRequest> cs2s(StreamObserver<HelloProto.HelloResponse> responseObserver) {
     //这里的return就是将这个observer给到了client   
     return new StreamObserver<HelloProto.HelloRequest>() {
            @Override
            public void onNext(HelloProto.HelloRequest helloRequest) {
                System.out.println("request "+ helloRequest.getName());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("finished");
                responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult("server end").build());
                responseObserver.onCompleted();
            }
        };
    }
```

### 双向流式rpc

api

```protobuf
  rpc cs2ss(stream HelloRequest) returns (stream HelloResponse){}
```

客户端和客户端流式rpc没有太大区别

服务端

```java
 /**
     * 双向流式rpc服务端
     * */
    @Override
    public StreamObserver<HelloProto.HelloRequest> cs2ss(StreamObserver<HelloProto.HelloResponse> responseObserver) {
        return new StreamObserver<HelloProto.HelloRequest>() {
            @Override
            public void onNext(HelloProto.HelloRequest helloRequest) {
                System.out.println("服务端 接收 "+ helloRequest.getName());
                //responseObserver.onNext多次调用即完成了流式传输
                responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult("服务端 回复 "+ helloRequest.getName()).build());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("服务端 接收 结束");
                responseObserver.onCompleted();
            }
        };
    }
```

