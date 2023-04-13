package io.en1s0o.hik.openapi;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * SSL 助手
 *
 * @author En1s0o
 */
@Slf4j
public class SSLHelper {

    private final X509TrustManager x509TrustManager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

    };

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public SSLSocketFactory getSSLContext() {
        // Install the all-trusting trust manager
        try {
            TrustManager[] trustManagers = new TrustManager[]{getX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustManagers, new SecureRandom());
            // Create a ssl socket factory with our all-trusting manager
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public HostnameVerifier getHostnameVerifier() {
        return (hostname, sslSession) -> {
            // needs verify?
            return true;
        };
    }

}
