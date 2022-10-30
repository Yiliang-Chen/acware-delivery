# AcWare Delivery 实时数据推送框架
- 框架说明
  - 基于 Netty 开发的 Websocket 实时数据推送框架，将数据直接通过 Websocket 推送至前端
  - 目前功能实现 
    - Websocket 接口配置化实现
    - Kafka Topic 订阅推送
    - 告警事件消息反馈（Email、Http）
  - 未来版本功能蓝图
    - 告警事件消息反馈（Email、Http）
- 使用场景
  - 双十一数据大屏展示、实时数据推送等实时数据使用场景

# 主要实现功能
- Kafka 数据实时推送
- 告警事件消息反馈（Email、Http）

# 版本功能
- 0.1.0 版本计划
  - Kafka 数据实时推送
  - 告警事件消息反馈(Email、Http)

- 当前版本已实现
  - 开发中（0.1.0）
    - Kafka 数据实时推送
    - 告警事件消息反馈（Email、Http）

# 现存问题
- @Sharable handler 无法同时打开多个连接
  - 添加：@ChannelHandler.Sharable 注解
# 源码编译
```shell
mvn clean package -Dmaven.test.skip=true
```
# 配置文件（delivery.properties）
```properties
callback.limit=5

kafka.poll-timeout=1000

netty.max-content-length=8192

email.smtp.hostname=smtp.sina.com
email.smtp.charset=UTF-8
email.smtp.authentication.username=AcWare@acware.top
email.smtp.authentication.password=PASSWORD
email.smtp.from.email=AcWare@acware.top
email.smtp.from.name=AcWare-Delivery

thread.pool.core-pool-size=6
thread.pool.max-pool-size=18
thread.pool.keep-alive-time=200
thread.pool.blocking-queue=java.util.concurrent.LinkedBlockingQueue

http.request.charset=UTF-8
http.request.timeout=60000
```
# 运行实现
# 仓库地址
- GitHub：https://github.com/Yiliang-Chen/acware-delivery
- Gitee：https://gitee.com/Yiliang-Chen/acware-delivery