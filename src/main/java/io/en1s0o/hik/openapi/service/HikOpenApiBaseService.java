package io.en1s0o.hik.openapi.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.en1s0o.hik.openapi.SSLHelper;
import io.en1s0o.hik.openapi.config.HikOpenApiConfiguration;
import io.en1s0o.hik.openapi.exception.HikOpenApiException;
import io.en1s0o.hik.openapi.utils.HikOpenApiSignUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 海康 OpenApi 基础服务
 *
 * @param <T> 必须是一个 interface
 * @author En1s0o
 */
@Slf4j
@SuppressWarnings({"UastIncorrectHttpHeaderInspection", "unused"})
public abstract class HikOpenApiBaseService<T> implements Interceptor {

    private static final String X_CA_TIMESTAMP = "x-ca-timestamp";
    private static final String X_CA_NONCE = "x-ca-nonce";
    private static final String X_CA_KEY = "x-ca-key";
    private static final String X_CA_SIGNATURE_HEADERS = "x-ca-signature-headers";
    private static final String X_CA_SIGNATURE = "x-ca-signature";

    protected static final ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final HikOpenApiConfiguration config;
    private final Class<T> apiClass;
    private T api;

    public HikOpenApiBaseService(HikOpenApiConfiguration config) {
        this.config = config;
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        // noinspection unchecked
        this.apiClass = (Class<T>) type.getActualTypeArguments()[0];
        assert this.apiClass.isInterface();

        refresh();
    }

    private OkHttpClient createOkHttpClient() {
        SSLHelper sslHelper = new SSLHelper();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout())
                .readTimeout(config.getReadTimeout())
                .writeTimeout(config.getWriteTimeout())
                .addNetworkInterceptor(this)
                .sslSocketFactory(sslHelper.getSSLContext(), sslHelper.getX509TrustManager())
                .hostnameVerifier(sslHelper.getHostnameVerifier());
        if (config.getLoggingLevel() != HttpLoggingInterceptor.Level.NONE) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(log::info);
            builder.addInterceptor(logging.setLevel(config.getLoggingLevel()));
        }
        if (config.getNetworkLoggingLevel() != HttpLoggingInterceptor.Level.NONE) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(log::info);
            builder.addNetworkInterceptor(logging.setLevel(config.getNetworkLoggingLevel()));
        }
        return builder.build();
    }

    public void refresh() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(config.getHost())
                .client(createOkHttpClient());
        for (Converter.Factory factory : getConverterFactories()) {
            builder.addConverterFactory(factory);
        }
        for (CallAdapter.Factory factory : getCallAdapterFactories()) {
            builder.addCallAdapterFactory(factory);
        }
        api = builder.build().create(apiClass);
    }

    @Nonnull
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request orgRequest = chain.request();

        HttpUrl url = orgRequest.url();
        String path = url.encodedPath();
        Map<String, String> queries = new HashMap<>();
        for (int i = 0; i < url.querySize(); i++) {
            queries.put(url.queryParameterName(i), url.queryParameterValue(i));
        }

        String timestamp = String.valueOf(new Date().getTime());
        String nonce = UUID.randomUUID().toString();
        Map<String, List<String>> headers = orgRequest.headers().toMultimap();
        headers.put(X_CA_TIMESTAMP, Collections.singletonList(timestamp));
        headers.put(X_CA_NONCE, Collections.singletonList(nonce));
        headers.put(X_CA_KEY, Collections.singletonList(config.getAppKey()));
        String sign = HikOpenApiSignUtils.sign(
                config.getAppSecret(),
                orgRequest.method(),
                path,
                headers,
                queries,
                null,
                new ArrayList<>());

        Request request = orgRequest.newBuilder()
                .header(X_CA_TIMESTAMP, timestamp)
                .header(X_CA_NONCE, nonce)
                .header(X_CA_KEY, config.getAppKey())
                .header(X_CA_SIGNATURE_HEADERS, headers.get(X_CA_SIGNATURE_HEADERS).get(0))
                .header(X_CA_SIGNATURE, sign)
                .build();
        return chain.proceed(request);
    }

    /**
     * 注意：添加多个转换器时，它们的顺序很重要，因为每个转换器必须能够处理前一个转换器生成的输出。
     *
     * @return 转换器工厂集合
     */
    @Nonnull
    protected Converter.Factory[] getConverterFactories() {
        return new Converter.Factory[]{
                ScalarsConverterFactory.create(),
                JacksonConverterFactory.create(mapper),
        };
    }

    @Nonnull
    protected CallAdapter.Factory[] getCallAdapterFactories() {
        return new CallAdapter.Factory[]{
                // GuavaCallAdapterFactory.create(),
        };
    }

    protected final T getApi() {
        return api;
    }

    protected final String getArtemis() {
        return config.getArtemis();
    }

    protected final <V> V syncCall(Call<V> call) throws IOException {
        Response<V> response = call.execute();
        V body = response.body();
        if (body != null || response.isSuccessful()) {
            return body;
        }
        try (ResponseBody errorBody = response.errorBody()) {
            if (errorBody != null) {
                throw new HikOpenApiException(errorBody.string());
            }
        }
        throw new HikOpenApiException();
    }

    protected final byte[] Response2Bytes(Response<ResponseBody> response) throws Exception {
        try (ResponseBody body = response.body()) {
            if (body != null) {
                return body.bytes();
            }
        }
        try (ResponseBody errorBody = response.errorBody()) {
            if (errorBody != null) {
                return errorBody.bytes();
            }
        }
        return null;
    }

}
