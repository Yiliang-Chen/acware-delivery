# AcWare Delivery 实时数据推送框架
- 框架说明
  - 基于 Netty 开发的 Websocket 实时数据推送框架，将数据直接通过 Websocket 推送至前端，目前可通过 Kafka、Http 接收数据
  - 功能实现 
    - Websocket 接口配置化实现
    - Kafka 数据实时推送
    - Http 数据实时推送
    - 预留告警事件消息反馈方法（Email、Http）
- 使用场景
  - 双十一数据大屏展示、实时数据推送等实时数据使用场景

# 版本更新记录
- 0.2.0
  - Config、Thread 代码优化，使用线程安全的 CopyOnWriteMap 类存储 channels
- 0.1.0
  - 首次发布版本
  - 新增 Kafka 数据实时推送
  - 新增 Http 数据实时推送
  - 新增 预留告警事件消息反馈方法（Email、Http）

# 版本功能
- 当前版本已实现
  - 0.2.0
  - 0.1.0
    - Kafka 数据实时推送
    - Http 数据实时推送
    - 预留告警事件消息反馈方法（Email、Http）

- 0.2.0 版本计划
  - [] Redis Callback
  - [] 代码优化

- 0.1.0 版本计划
  - [√] Kafka 数据实时推送
  - [√] Http 数据实时推送
  - [√] 预留告警事件消息反馈方法（Email、Http）

- 未来版本功能蓝图
  - Socket 数据实时推送
  - 其他消息队列数据实时推送（ActiveMQ、RabbitMQ、RocketMQ 等）
  - 数据库更新实时推送

# 源码编译
```shell
mvn clean package -Dmaven.test.skip=true
```
# 配置文件参考
## delivery.properties
```properties
# 回调函数刷新条数
callback.limit=5

# Kafka 拉取数据超时时间
kafka.poll-timeout=1000

# Netty 接收数据最大长度
netty.max-content-length=8192

# Email smtp 的地址
email.smtp.hostname=smtp.sina.com
email.smtp.charset=UTF-8
# 发送邮件的账号
email.smtp.authentication.username=AcWare@acware.top
# 发送邮件的密码(有些是授权码)
email.smtp.authentication.password=PASSWORD
# 发送邮件的账号(需要和上面一致)
email.smtp.from.email=AcWare@acware.top
# 发送邮件的账号名称
email.smtp.from.name=AcWare-Delivery

# 线程池核心线程大小
thread.pool.core-pool-size=6
# 线程池最大线程数量
thread.pool.max-pool-size=18
# 线程池多余的空闲线程存活时间
thread.pool.keep-alive-time=200
# 线程池任务队列大小
thread.pool.queue-size=-1

http.request.charset=UTF-8
# Http 请求超时时间
http.request.timeout=60000
```
## 日志打印
```properties
log4j.rootLogger=DEBUG, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[%d] [%t] %p %l %m%n

log4j.logger.org.apache.kafka=WARN
```
# 运行实现
- 测试类 src/test/java/top/acware/delivery/example/example.java
- 流程
  - 添加 delivery.properties 配置文件
    - 需要 Email 功能需要修改 Email 配置的相关内容
    - 有定制化需求自行修改配置文件
  - 创建回调函数 Callback
    - 自行选择需要的回调函数实现类或自行实现
  - 创建告警方式 Warning(可选)
    - 根据需求自行配置相关内容
  - 创建 WebsocketServerWorker 类
    - 配置 websocket 的路径、端口、线程使用个数(可选)
    - 如果不配置的话无法选择默认配置，需要自行实现 handler
    - 如果需要默认配置的 handler，需要创建一个默认的 handler，传入发送数据的方法，如果需要使用告警功能可以自行实现告警规则
      - 需要调用 setDefaultChildHandler()
    - 创建完成之后可以使用 ThreadPool 线程池运行线程
  - 创建数据接收的 worker(KafkaConsumerWorker/HttpReceiveWorker)
    - kafka 需要传入对应的 KafkaConsumer 和 Callback(可自行实现)
    - Http 需要传入对应的端口、请求方式、URI、Callback(可自行实现)，配置 handler
    - 创建完成之后可以使用 ThreadPool 线程池运行线程
# 仓库地址
- GitHub：https://github.com/Yiliang-Chen/acware-delivery
- Gitee：https://gitee.com/Yiliang-Chen/acware-delivery