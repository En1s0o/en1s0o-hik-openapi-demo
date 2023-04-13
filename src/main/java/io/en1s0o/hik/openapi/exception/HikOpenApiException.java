package io.en1s0o.hik.openapi.exception;

/**
 * 海康 OpenApi 调用异常
 *
 * @author En1s0o
 */
public class HikOpenApiException extends RuntimeException {

    public HikOpenApiException() {
        super();
    }

    public HikOpenApiException(String message) {
        super(message);
    }

}
