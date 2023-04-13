package io.en1s0o.hik.openapi.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 海康 OpenApi 签名工具
 *
 * @author En1s0o
 */
public class HikOpenApiSignUtils {

    public static String sign(
            String secret,
            String method,
            String path,
            Map<String, List<String>> headers,
            Map<String, String> queries,
            Map<String, String> bodies,
            List<String> signHeaderPrefixList) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256"));
            byte[] data = buildStringToSign(method, path, headers, queries, bodies, signHeaderPrefixList).getBytes(StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(hmacSha256.doFinal(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildStringToSign(
            String method,
            String path,
            Map<String, List<String>> headers,
            Map<String, String> queries,
            Map<String, String> bodies,
            List<String> signHeaderPrefixList) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.toUpperCase()).append("\n");
        if (null != headers) {
            String accept = getHeader(headers, "Accept");
            if (!isBlank(accept)) {
                sb.append(accept);
            }
            String contentMd5 = getHeader(headers, "Content-MD5");
            if (!isBlank(contentMd5)) {
                sb.append(contentMd5);
            }
            String contentType = getHeader(headers, "Content-Type");
            if (!isBlank(contentType)) {
                sb.append(contentType);
            }
            String date = getHeader(headers, "Date");
            if (!isBlank(date)) {
                sb.append(date);
            }
        }

        sb.append(buildHeaders(headers, signHeaderPrefixList));
        sb.append(buildResource(path, queries, bodies));
        return sb.toString();
    }

    private static String getHeader(Map<String, List<String>> headers, String key) {
        List<String> header = headers.get(key);
        if (header != null && !header.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String item : header) {
                if (!isBlank(item)) {
                    sb.append(item);
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
        return null;
    }

    private static String buildHeaders(Map<String, List<String>> headers, List<String> signHeaderPrefixList) {
        if (null != signHeaderPrefixList) {
            signHeaderPrefixList.remove("x-ca-signature");
            signHeaderPrefixList.remove("Accept");
            signHeaderPrefixList.remove("Content-MD5");
            signHeaderPrefixList.remove("Content-Type");
            signHeaderPrefixList.remove("Date");
            Collections.sort(signHeaderPrefixList);
        }

        StringBuilder sb = new StringBuilder();
        if (null != headers) {
            StringBuilder signHeadersStringBuilder = new StringBuilder();
            headers.forEach((key, value) -> {
                if (isHeaderToSign(key, signHeaderPrefixList)) {
                    if (value != null && !value.isEmpty()) {
                        for (String item : value) {
                            sb.append(key);
                            sb.append(":");
                            if (!isBlank(item)) {
                                sb.append(item);
                            }
                            sb.append("\n");
                        }
                    } else {
                        sb.append(key);
                        sb.append(":\n");
                    }

                    if (0 < signHeadersStringBuilder.length()) {
                        signHeadersStringBuilder.append(",");
                    }

                    signHeadersStringBuilder.append(key);
                }
            });
            headers.put("x-ca-signature-headers", Collections.singletonList(signHeadersStringBuilder.toString()));
        }
        return sb.toString();
    }

    private static boolean isHeaderToSign(String headerName, List<String> signHeaderPrefixList) {
        if (isBlank(headerName)) {
            return false;
        } else if (headerName.startsWith("x-ca-")) {
            return true;
        } else if (null != signHeaderPrefixList) {
            for (String signHeaderPrefix : signHeaderPrefixList) {
                if (headerName.equalsIgnoreCase(signHeaderPrefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String buildResource(
            String path,
            Map<String, String> queries,
            Map<String, String> bodies) {
        StringBuilder sb = new StringBuilder();
        if (!isBlank(path)) {
            sb.append(path);
        }

        Map<String, String> sortMap = new TreeMap<>();
        if (null != queries) {
            queries.forEach((key, value) -> {
                if (!isBlank(key)) {
                    sortMap.put(key, value);
                }
            });
        }

        if (null != bodies) {
            bodies.forEach((key, value) -> {
                if (!isBlank(key)) {
                    sortMap.put(key, value);
                }
            });
        }

        StringBuilder sbParam = new StringBuilder();
        sortMap.forEach((key, value) -> {
            if (!isBlank(key)) {
                if (0 < sbParam.length()) {
                    sbParam.append("&");
                }

                sbParam.append(key);
                if (!isBlank(value)) {
                    sbParam.append("=").append(value);
                }
            }
        });

        if (0 < sbParam.length()) {
            sb.append("?");
            sb.append(sbParam);
        }

        return sb.toString();
    }

    private static boolean isBlank(final CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen != 0) {
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

}
