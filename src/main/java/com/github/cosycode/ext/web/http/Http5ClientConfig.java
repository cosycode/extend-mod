package com.github.cosycode.ext.web.http;

import com.github.cosycode.common.ext.hub.LazySingleton;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpHost;
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
 * @author CPF
 * @since 1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Http5ClientConfig {

    private static final int MAX_CONN_TOTAL = 200;
    private static final int CONNECT_TIME_OUT = 120;

    private static final LazySingleton<CloseableHttpClient> defaultCloseableHttpClient = LazySingleton.of(Http5ClientConfig::defaultHttpClient);

    /**
     * 从池子中获取连接
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getCloseableHttpClient() {
        return defaultCloseableHttpClient.instance();
    }

    public static CloseableHttpClient defaultHttpClient() {
        return defaultHttpClientBuilder().build();
    }

    /**
     * HttpClient 5 Basic Authorization
     *
     * <p>
     *    <a href="https://hc.apache.org/httpcomponents-client-5.2.x/quickstart.html">quickstart</a>
     * </p>
     * @param proxyServerDomain proxy ip or domain
     * @param port proxy port
     * @param username Basic Authorization username
     * @param password Basic Authorization password
     * @return HttpClientBuilder
     */
    public static HttpClientBuilder geneHttpClientBuilderWithBasicProxyAuthorization(String proxyServerDomain, int port, String username, String password) {
        // build DefaultCredentialsProvider
        AuthScope authScope = new AuthScope(proxyServerDomain, port);
        Credentials proxyCredentials = new UsernamePasswordCredentials(username, password.toCharArray());
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(authScope, proxyCredentials);
        // build Client
        final HttpHost proxy = new HttpHost(proxyServerDomain, port);
        final DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        return defaultHttpClientBuilder().setDefaultCredentialsProvider(credentialsProvider).setRoutePlanner(routePlanner);
    }

    /**
     * HttpClient 5 Base on Proxy
     * @param proxyServerDomain proxy ip or domain
     * @param port proxy port
     * @return HttpClientBuilder
     */
    public static HttpClientBuilder geneHttpClientBuilderWithProxy(String proxyServerDomain, int port) {
        return defaultHttpClientBuilder().setProxy(new HttpHost(proxyServerDomain, port));
    }

    /**
     * @return default HttpClientBuilder
     */
    public static HttpClientBuilder defaultHttpClientBuilder() {
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
                );
    }

    public static HttpClientConnectionManager defaultHttpClientConnectionManager() {
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
     * custom ssl check, to skip the authentication of ssl, to avoid the exception: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
     */
    @SuppressWarnings("all")
    public static SSLConnectionSocketFactory getSSLFactory() {
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