package com.github.cosycode.ext.web.http;

import com.github.cosycode.common.ext.hub.LazySingleton;
import com.github.cosycode.ext.io.cache.AbstractKeyCacheHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <b>Description : </b> 生产环境推荐一个服务使用一个 Client
 * <p>
 * <b>created in </b> 2023/5/14
 * </p>
 *
 * @author CPF
 **/
@Slf4j
@Setter
@Getter
public class MyHttpClient {

    public static final LazySingleton<MyHttpClient> DEFAULT_INSTANCE = LazySingleton.of(() -> {
        MyHttpClient myHttpClient = new MyHttpClient(Http5ClientConfig.getCloseableHttpClient(), MyHttpResponse.DEFAULT_HANDLER);
        myHttpClient.setWebCacheHandler(null);
        myHttpClient.setPreProcess(req -> log.info("[{}] {}", req.method(), req.requestUrl()));
        myHttpClient.setSufProcess((req, resp) -> {
            if (resp.isCode(404)) {
                log.warn("[{}] {} ==> {}", req.method(), req.requestUrl(), resp.data());
            }
        });
        return myHttpClient;
    });
    /**
     * 封装的 Http5Client
     */
    private final CloseableHttpClient closeableHttpClient;
    /**
     * 用于解析 response
     */
    private final HttpClientResponseHandler<MyHttpResponse> httpClientResponseHandler;
    /**
     * 用于缓存, 是否使用缓存
     */
    private AbstractKeyCacheHandler<MyHttpRequest, MyHttpResponse> webCacheHandler;
    /**
     * 前置处理
     */
    private Consumer<MyHttpRequest> preProcess;
    /**
     * 后置处理
     */
    private BiConsumer<MyHttpRequest, MyHttpResponse> sufProcess;

    public MyHttpClient(CloseableHttpClient closeableHttpClient, HttpClientResponseHandler<MyHttpResponse> httpClientResponseHandler) {
        this.closeableHttpClient = closeableHttpClient;
        this.httpClientResponseHandler = httpClientResponseHandler;
    }

    /**
     * 发送之前, 经过 preProcess 进行处理, 之后将消息转给 send 方法
     */
    public static MyHttpResponse send(MyHttpRequest myHttpRequest, CloseableHttpClient closeableHttpClient,
                                      AbstractKeyCacheHandler<MyHttpRequest, MyHttpResponse> webCacheHandler,
                                      HttpClientResponseHandler<MyHttpResponse> httpClientResponseHandler,
                                      Consumer<MyHttpRequest> preProcess,
                                      BiConsumer<MyHttpRequest, MyHttpResponse> sufProcess) throws IOException {
        // 前置处理
        if (preProcess != null) {
            preProcess.accept(myHttpRequest);
        }
        // 发送请求
        final MyHttpResponse myHttpResponse;
        if (webCacheHandler == null) {
            myHttpResponse = MyHttpClient.doSend(myHttpRequest, closeableHttpClient, httpClientResponseHandler);
        } else {
            myHttpResponse = webCacheHandler.computeIfAbsent(myHttpRequest, () -> MyHttpClient.doSend(myHttpRequest, closeableHttpClient, httpClientResponseHandler), r -> !r.isCode(429));
        }
        // 后置处理
        if (sufProcess != null) {
            sufProcess.accept(myHttpRequest, myHttpResponse);
        }
        return myHttpResponse;
    }

    private static <R> R doSend(MyHttpRequest request, CloseableHttpClient closeableHttpClient, HttpClientResponseHandler<R> httpClientResponseHandler) throws IOException {
        return HttpUtils.http(closeableHttpClient, request.method(), request.requestUrl(),
                request.headers(), request.params(), request.jsonBody(), httpClientResponseHandler);
    }

    /**
     * 发送之前, 经过 preProcess 进行处理, 之后将消息转给 send 方法
     */
    public MyHttpResponse send(MyHttpRequest myHttpRequest) throws IOException {
        return send(myHttpRequest, closeableHttpClient, webCacheHandler, httpClientResponseHandler, preProcess, sufProcess);
    }

    public MyHttpResponse download(MyHttpRequest myHttpRequest, String savePath) throws IOException {
        MyHttpDownloadResponseHandler responseHandler = new MyHttpDownloadResponseHandler(savePath);
        return send(myHttpRequest, closeableHttpClient, null, responseHandler, preProcess, null);
    }

}
