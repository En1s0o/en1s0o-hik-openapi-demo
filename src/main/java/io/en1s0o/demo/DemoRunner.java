package io.en1s0o.demo;

import io.en1s0o.demo.domain.EventSubscriptionRequest;
import io.en1s0o.demo.domain.EventSubscriptionResponse;
import io.en1s0o.demo.domain.EventSubscriptionViewRequest;
import io.en1s0o.demo.domain.EventSubscriptionViewResponse;
import io.en1s0o.demo.domain.EventUnSubscriptionRequest;
import io.en1s0o.demo.domain.EventUnSubscriptionResponse;
import io.en1s0o.demo.service.HikOpenApiEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 测试
 *
 * @author En1s0o
 */
@Slf4j
@Component
public class DemoRunner implements ApplicationRunner {

    private final HikOpenApiEventService service;

    public DemoRunner(HikOpenApiEventService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 调用【智能视频赋能获取获取所有的父事件类型】得到我们关心的事件
        // {
        //     "eventName": "人员聚集视频检测",
        //     "eventCode": "0x0100000013000400"
        // }, {
        //     "eventName": "非机动车乱停检测",
        //     "eventCode": "0x0100000006000100"
        // }
        List<Long> eventTypes = Arrays.asList(
                Long.decode("0x0100000013000400"),
                Long.decode("0x0100000006000100"));
        {
            // 订阅
            EventSubscriptionRequest req = EventSubscriptionRequest.builder()
                    .eventTypes(eventTypes)
                    .subWay(1)
                    .eventDest("http://192.168.205.126:8080/hik/onEvent")
                    .build();
            EventSubscriptionResponse resp = service.eventSubscriptionByEventTypes(req);
            log.info("resp={}", resp);
        }
        {
            // 查看订阅
            EventSubscriptionViewRequest req = EventSubscriptionViewRequest.builder().subWay(1).build();
            EventSubscriptionViewResponse resp = service.eventSubscriptionView(req);
            log.info("resp={}", resp);
        }
        {
            // 取消订阅
            // EventUnSubscriptionRequest req = EventUnSubscriptionRequest.builder().eventTypes(eventTypes).build();
            // EventUnSubscriptionResponse resp = service.eventUnSubscriptionByEventTypes(req);
            // log.info("resp={}", resp);
        }
    }

}
