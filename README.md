# AcWare Delivery 实时数据推送框架
- 框架说明
  - 基于 Netty 开发的 Websocket 实时数据推送框架，将数据直接通过 Websocket 推送至前端，目前可通过 Kafka、Http 接收数据
  - 功能实现 
    - Websocket 接口配置化实现
    - Kafka 数据实时推送
    - Http 数据实时推送
    - 预留告警事件消息反馈方法（Email、Http）
    - Redis 缓存数据
- 使用场景
  - 双十一数据大屏展示、实时数据推送等实时数据使用场景

# 版本更新记录
- 1.0.0
  - 重构历史版本，使用 JSON 格式配置文件
  - 通过配置形式制定告警规则
  - 配置后可直接部署，无需单独开发
- 0.2.0
  - Config、Thread 代码优化，使用线程安全的 CopyOnWriteMap 类存储 channels
  - Redis Callback
- 0.1.0
  - 首次发布版本
  - 新增 Kafka 数据实时推送
  - 新增 Http 数据实时推送
  - 新增 预留告警事件消息反馈方法（Email、Http）

# 版本功能
- 当前版本已实现
  - 1.0.0
    - 直接部署模式
    - 模块重构
  - 0.2.0
    - Redis Callback
  - 0.1.0
    - Kafka 数据实时推送
    - Http 数据实时推送
    - 预留告警事件消息反馈方法（Email、Http）
- 1.0.0 版本计划
  - [√] 直接部署模式
  - [√] 模块重构
  - [] MySQL 更新监听

- 0.2.0 版本计划
  - [√] Redis Callback
  - [√] 代码优化

- 0.1.0 版本计划
  - [√] Kafka 数据实时推送
  - [√] Http 数据实时推送
  - [√] 预留告警事件消息反馈方法（Email、Http）

# 源码编译
```shell
mvn clean package -DMaven.test.skip=true
```
# 配置文件说明
## server.json
```json5
{
  "server": {
    // 标明下方模块名字，用 ',' 隔开
    "module": "example-1",
    "global": {
      // 线程池配置
      "thread-pool": {
        "core-pool.size": 6,
        "max-pool.size": 18,
        "keep-alive.time": 200,
        // 0 使用 SynchronousQueue，大于 0 使用 ArrayBlockingQueue 并配置对应的大小，小于 0 配置 LinkedBlockingQueue
        "queue.size": 0
      }
    }
  },
  "example-1": {
    // callback 数据缓存选项，暂时只支持 ["cache","redis"]，单选
    "callback": "cache",
    // callback 缓存数量
    "callback_limit": 5,
    // 接收端模块配置，用 ',' 隔开
    "receive": "kafka-example,http-example",
    "kafka-example": {
      // 当前模块使用的类型，暂时只支持 ["kafka","http"]，单选
      "type": "kafka",
      // 如果是 kafka，添加下方的配置
      "topic": "kafka",
      "poll_timeout": 1000,
      // kafka 的消费者配置
      "properties": {
        "bootstrap.servers": "localhost:9092",
        "group.id": "example",
        "enable.auto.commit": true,
        "auto.commit.interval.ms": 1000,
        "session.timeout.ms": 30000,
        "key.deserializer": "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer": "org.apache.kafka.common.serialization.StringDeserializer"
      }
    },
    "http-example": {
      // http 接收端配置
      "type": "http",
      "bossThreads": 1,
      "workerThreads": 1,
      // 只接收该 uri 的数据
      "uri": "/data",
      "port": 9998,
      // 只接收该 method 的数据
      "method": "POST",
      "max.content.length": 8192
    },
    // websocket 配置
    "websocket": {
      "bossThreads": 1,
      "workerThreads": 8,
      "uri": "/ws",
      "port": 9999,
      "max.content.length": 8192
    },
    // 告警规则配置，可选
    "warning": {
      "global": {
        // email 全局配置，目前只有 email 可配置全局配置
        "email": {
          "smtp.hostname": "smtp.sina.com",
          "smtp.charset": "UTF-8",
          "smtp.authentication.username": "coca_open@sina.com",
          "smtp.authentication.password": "11c6527711726503",
          "smtp.from.email": "coca_open@sina.com",
          "smtp.from.name": "AcWare-Delivery"
        }
      },
      // 规则配置
      "rule": {
        // 标明下方模块名字，用 ',' 隔开
        "module": "minus,illegality",
        "minus": {
          // 匹配规则，需要是返回 boolean 值的表达式
          "expression": "record.getValue() >= '0'",
          "executor": {
            // 标明下方模块名字，用 ',' 隔开
            "module": "email",
            "email": {
              // 告警模式，暂时只支持 ["email","http"]，单选，可多配
              "pattern": "email",
              "smtp.subject": "AcWare-Delivery 数据负数告警",
              "smtp.to": "18177410488@163.com,1982455737@qq.com",
              "smtp.cc": "2720602488@qq.com",
              // 需要添加 %s 接收该条数据
              "warn.context": "出现负数：%s"
            }
          }
        },
        "illegality": {
          "expression": "record.getValue().matches(\"^-?[0-9]+(.[0-9]+)?$\")",
          "executor": {
            // 多配置样例
            "module": "email-method,http-method",
            "email-method": {
              "pattern": "email",
              "smtp.subject": "AcWare-Delivery 数据非法告警",
              "smtp.to": "18177410488@163.com,1982455737@qq.com",
              "smtp.cc": "2720602488@qq.com",
              "warn.context": "非法数据：%s"
            },
            // http 告警配置
            "http-method": {
              "pattern": "http",
              "url": "https://www.baidu.com",
              // 暂时只支持 ["GET","POST"]，单选
              "method": "GET",
              "headers": {
                "Content-type": "application/json"
              },
              // data 下的 content 固定写法，需要配置 %s，其他可自定义
              "data": {
                "content": "非法数据：%s",
                "fields": "field"
              }
            }
          }
        }
      }
    }
  }
}
```

# 运行实现
- 根据实际情况修改 json 配置
- 启动 bin/delivery-start.sh
- 停止 bin/delivery-stop.sh

# 仓库地址
- GitHub：https://github.com/Yiliang-Chen/acware-delivery
- Gitee：https://gitee.com/Yiliang-Chen/acware-delivery