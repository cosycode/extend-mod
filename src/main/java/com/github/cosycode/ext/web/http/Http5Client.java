package com.github.cosycode.ext.web.http;

import com.github.cosycode.common.ext.hub.LazySingleton;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/12/27
 * </p>
 *
 * @author pengfchen
 * @since 1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Http5Client {

    private static final int MAX_CONN_TOTAL = 200;
    private static final int CONNECT_TIME_OUT = 120;

    private static LazySingleton<CloseableHttpClient> defaultCloseableHttpClient = LazySingleton.of(Http5Client::defaultHttpClientBuilder);

    /**
     * 从池子中获取连接
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getCloseableHttpClient() {
        return defaultCloseableHttpClient.instance();
    }

    private static CloseableHttpClient defaultHttpClientBuilder() {
        return HttpClientBuilder.create()
            .setConnectionManager(defaultHttpClientConnectionManager())
            .evictIdleConnections(TimeValue.ofMinutes(1))
            .disableAutomaticRetries()
            // set ConnectManager to shared, the Connection will not be closed, otherwise the exception may be thrown: `Connection pool shut down`
            .setConnectionManagerShared(true)
            // Set to persistent connection.
            .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setResponseTimeout(180, TimeUnit.SECONDS)
                    .setConnectionRequestTimeout(30, TimeUnit.SECONDS)
                    .build()
            ).build();
    }

    private static HttpClientConnectionManager defaultHttpClientConnectionManager() {
        return PoolingHttpClientConnectionManagerBuilder.create()
            .setSSLSocketFactory(getSSLFactory())
            .setMaxConnPerRoute(MAX_CONN_TOTAL - 1)
            .setMaxConnTotal(MAX_CONN_TOTAL)
            .setDefaultConnectionConfig(
                ConnectionConfig.custom()
                    .setConnectTimeout(Timeout.ofSeconds(CONNECT_TIME_OUT))
                    .setValidateAfterInactivity(TimeValue.ofSeconds(10))
                    .build()
            )
            .setDefaultSocketConfig(
                SocketConfig.custom()
                    .setSoTimeout(5, TimeUnit.SECONDS)
                    .build()
            ).build();
    }

    /**
     * 自定义 ssl check.
     * <p>
     * 跳过 ssl 认证, 解决报错 sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
     */
    @SuppressWarnings("all")
    private static SSLConnectionSocketFactory getSSLFactory() {
        X509ExtendedTrustManager trustManager = new X509ExtendedTrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{trustManager}, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        assert ctx != null;
        return new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
    }
}