package com.github.cosycode.ext.web.http;

import com.github.cosycode.ext.io.cache.AbstractKeyCacheHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Method;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class HttpHelper {

    public static AbstractKeyCacheHandler<MyHttpRequest, MyHttpResponse> webCacheHandler;

    public static MyHttpRequest buildGet(String url) {
        return new MyHttpRequest(Method.GET.name(), url);
    }

    public static MyHttpRequest buildPut(String url) {
        return new MyHttpRequest(Method.PUT.name(), url);
    }

    public static MyHttpRequest buildPost(String url) {
        return new MyHttpRequest(Method.POST.name(), url);
    }

    public static MyHttpRequest buildDelete(String url) {
        return new MyHttpRequest(Method.DELETE.name(), url);
    }

    public static MyHttpRequest buildPatch(String url) {
        return new MyHttpRequest(Method.PATCH.name(), url);
    }

    @Data
    @Accessors(fluent = true)
    public static class MyHttpRequest {

        private String method;
        private Map<String, Object> headers;
        private String requestUrl;
        private Map<String, String> params;
        private Object jsonBody;

        public MyHttpRequest(String method, String requestUrl) {
            this.method = method;
            this.requestUrl = requestUrl;
        }

        public MyHttpResponse send(Consumer<MyHttpRequest> preProcess) throws IOException {
            if (preProcess != null) {
                preProcess.accept(this);
            }
            MyHttpResponse myHttpResponse = send();
            if (myHttpResponse.isCode(404)) {
                log.warn("[{}] {} ==> {}", method, requestUrl, myHttpResponse.data());
            }
            return myHttpResponse;
        }

        public MyHttpResponse send() throws IOException {
            if (webCacheHandler == null) {
                return doSend();
            } else {
                return webCacheHandler.computeIfAbsent(this, this::doSend);
            }
        }

        private MyHttpResponse doSend() throws IOException {
            return HttpUtils.http(method, requestUrl, headers, params, jsonBody, MyHttpResponse.DEFAULT_HANDLER);
        }

    }


}