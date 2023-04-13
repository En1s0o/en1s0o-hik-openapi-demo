package io.en1s0o.demo.service;

import io.en1s0o.demo.domain.EventSubscriptionRequest;
import io.en1s0o.demo.domain.EventSubscriptionResponse;
import io.en1s0o.demo.domain.EventSubscriptionViewRequest;
import io.en1s0o.demo.domain.EventSubscriptionViewResponse;
import io.en1s0o.demo.domain.EventUnSubscriptionRequest;
import io.en1s0o.demo.domain.EventUnSubscriptionResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.io.IOException;

/**
 * 海康 OpenApi 事件服务
 *
 * @author En1s0o
 */
public interface HikOpenApiEventService {

    interface ApiStub {

        /**
         * 按事件类型订阅事件
         *
         * @param artemis artemis 路径
         * @param body    事件订阅请求数据结构
         * @return 调用对象
         */
        @Headers({"Accept: */*", "Content-Type: application/json;charset=UTF-8"})
        @POST("/{artemis}/api/eventService/v1/eventSubscriptionByEventTypes")
        Call<EventSubscriptionResponse> eventSubscriptionByEventTypes(
                @Path("artemis") String artemis,
                @Body EventSubscriptionRequest body);

        /**
         * 按事件类型取消订阅
         *
         * @param artemis artemis 路径
         * @param body    事件取消订阅请求数据结构
         * @return 调用对象
         */
        @Headers({"Accept: */*", "Content-Type: application/json;charset=UTF-8"})
        @POST("/{artemis}/api/eventService/v1/eventUnSubscriptionByEventTypes")
        Call<EventUnSubscriptionResponse> eventUnSubscriptionByEventTypes(
                @Path("artemis") String artemis,
                @Body EventUnSubscriptionRequest body);

        /**
         * 按用户查询事件订阅信息
         *
         * @param artemis artemis 路径
         * @param body    事件订阅视图请求数据结构
         * @return 调用对象
         */
        @Headers({"Accept: */*", "Content-Type: application/json;charset=UTF-8"})
        @POST("/{artemis}/api/eventService/v1/eventSubscriptionView")
        Call<EventSubscriptionViewResponse> eventSubscriptionView(
                @Path("artemis") String artemis,
                @Body EventSubscriptionViewRequest body);

    }

    /**
     * 按事件类型订阅事件
     *
     * @param req 订阅请求体
     * @return 事件订阅响应数据结构
     * @throws IOException 订阅异常
     */
    EventSubscriptionResponse eventSubscriptionByEventTypes(EventSubscriptionRequest req) throws IOException;

    /**
     * 按事件类型取消订阅
     *
     * @param req 取消订阅请求体
     * @return 事件取消订阅响应数据结构
     * @throws IOException 取消订阅异常
     */
    EventUnSubscriptionResponse eventUnSubscriptionByEventTypes(EventUnSubscriptionRequest req) throws IOException;

    /**
     * 按用户查询事件订阅信息
     *
     * @param req 事件订阅视图请求数据结构
     * @return 事件订阅视图响应数据结构
     * @throws IOException 获取订阅信息异常
     */
    EventSubscriptionViewResponse eventSubscriptionView(EventSubscriptionViewRequest req) throws IOException;

}
