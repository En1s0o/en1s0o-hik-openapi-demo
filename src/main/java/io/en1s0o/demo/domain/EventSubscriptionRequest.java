package io.en1s0o.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 事件订阅请求数据结构
 *
 * @author En1s0o
 */
@Data
@Builder(builderClassName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventSubscriptionRequest {

    @JsonProperty("eventTypes")
    private List<Long> eventTypes;

    @JsonProperty("subWay")
    private Integer subWay;

    @JsonProperty("eventDest")
    private String eventDest;

    @JsonProperty("subType")
    private Integer subType;

    @JsonProperty("eventLvl")
    private List<Integer> eventLvl;

}
