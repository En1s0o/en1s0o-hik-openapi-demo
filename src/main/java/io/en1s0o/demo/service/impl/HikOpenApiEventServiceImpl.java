package io.en1s0o.demo.service.impl;

import io.en1s0o.hik.openapi.config.HikOpenApiConfiguration;
import io.en1s0o.demo.domain.EventSubscriptionRequest;
import io.en1s0o.demo.domain.EventSubscriptionResponse;
import io.en1s0o.demo.domain.EventSubscriptionViewRequest;
import io.en1s0o.demo.domain.EventSubscriptionViewResponse;
import io.en1s0o.demo.domain.EventUnSubscriptionRequest;
import io.en1s0o.demo.domain.EventUnSubscriptionResponse;
import io.en1s0o.hik.openapi.service.HikOpenApiBaseService;
import io.en1s0o.demo.service.HikOpenApiEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 海康 OpenApi 事件服务实现
 *
 * @author En1s0o
 */
@Slf4j
@Service
@EnableConfigurationProperties(HikOpenApiConfiguration.class)
public class HikOpenApiEventServiceImpl extends HikOpenApiBaseService<HikOpenApiEventService.ApiStub>
        implements HikOpenApiEventService {

    public HikOpenApiEventServiceImpl(HikOpenApiConfiguration hikOpenApiConfig) {
        super(hikOpenApiConfig);
    }

    @Override
    public EventSubscriptionResponse eventSubscriptionByEventTypes(EventSubscriptionRequest req) throws IOException {
        return syncCall(getApi().eventSubscriptionByEventTypes(getArtemis(), req));
    }

    @Override
    public EventUnSubscriptionResponse eventUnSubscriptionByEventTypes(EventUnSubscriptionRequest req) throws IOException {
        return syncCall(getApi().eventUnSubscriptionByEventTypes(getArtemis(), req));
    }

    @Override
    public EventSubscriptionViewResponse eventSubscriptionView(EventSubscriptionViewRequest req) throws IOException {
        return syncCall(getApi().eventSubscriptionView(getArtemis(), req));
    }

}
