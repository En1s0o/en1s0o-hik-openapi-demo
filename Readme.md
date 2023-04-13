# 海康 OpenApi 封装

这个工程的主要目的是简化海康 OpenApi 对接。

为什么不使用海康提供的 SDK？因为这个更优雅！



## 快速开始

- 修改 application.yml 配置

  > 访问 API 网关，例如：http://192.168.205.1:9017/artemis-web/consumer/index 获取到合作方的 key 和 secret

  ```yaml
  hik:
    open-api:
      host: https://192.168.205.1:443
      artemis: artemis
      app-key: 25699962
      app-secret: TkZeXuKdqddmWigKsHMD
      network-logging-level: BODY
  ```

- 启动 DemoApplication



## 核心功能

代码位于 io.en1s0o.hik.openapi，通常不需要修改。

它实现了海康 OpenApi 鉴权，并提供抽象类 HikOpenApiBaseService，它可以让你专心写扩展。



## 扩展功能

海康的 OpenApi 非常多，根据项目按需扩展即可，没有必要过度扩展。

扩展主要做 2 件事情：

- 在 domain 定义 DTO、VO
- 在 service 定义接口，并实现它



### 扩展事件订阅示例

在 domain 定义（也不是必须的，如果你觉得麻烦，用 String 代替一切，也不是不能用）

- EventSubscriptionRequest
- EventSubscriptionResponse
- EventUnSubscriptionRequest
- EventUnSubscriptionResponse
- EventSubscriptionViewRequest
- EventSubscriptionViewResponse

在 service 定义

- HikOpenApiEventService（通常和 ApiStub 相仿）

  - ApiStub（Retrofit2 接口定义）

- HikOpenApiEventServiceImpl（实现）

  ```java
  public class HikOpenApiEventServiceImpl
      extends HikOpenApiBaseService<HikOpenApiEventService.ApiStub>
      implements HikOpenApiEventService
  ```



