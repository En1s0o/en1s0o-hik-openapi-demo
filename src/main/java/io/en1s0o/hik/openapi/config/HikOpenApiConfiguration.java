package io.en1s0o.hik.openapi.config;

import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 海康 OpenApi 配置
 *
 * @author En1s0o
 */
@Data
@Configuration
@ConfigurationProperties(prefix = HikOpenApiConfiguration.PREFIX)
public class HikOpenApiConfiguration {

    public static final String PREFIX = "hik.open-api";

    // 服务器地址
    private String host;

    // artemis 路径
    private String artemis;

    // appKey
    private String appKey;

    // appSecret
    private String appSecret;

    // http 链接超时时间
    private Duration connectTimeout = Duration.ofSeconds(30);

    // http 读取超时时间
    private Duration readTimeout = Duration.ofSeconds(30);

    // http 写入超时时间
    private Duration writeTimeout = Duration.ofSeconds(30);

    // http 应用日志级别
    private HttpLoggingInterceptor.Level loggingLevel = HttpLoggingInterceptor.Level.NONE;

    // http 网络日志级别
    private HttpLoggingInterceptor.Level networkLoggingLevel = HttpLoggingInterceptor.Level.NONE;

}
