package com.github.cosycode.ext.web.http;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Method;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <b>Description : </b> utils for http
 * <p>
 * <b>created in </b> 2022/12
 *
 * @author CPF
 **/
@Slf4j
public class HttpHelper {

    private HttpHelper() {
    }

    public static MyHttpRequestHelper buildGet(String url) {
        return new MyHttpRequestHelper(Method.GET.name(), url);
    }

    public static MyHttpRequestHelper buildPut(String url) {
        return new MyHttpRequestHelper(Method.PUT.name(), url);
    }

    public static MyHttpRequestHelper buildPost(String url) {
        return new MyHttpRequestHelper(Method.POST.name(), url);
    }

    public static MyHttpRequestHelper buildDelete(String url) {
        return new MyHttpRequestHelper(Method.DELETE.name(), url);
    }

    public static MyHttpRequestHelper buildPatch(String url) {
        return new MyHttpRequestHelper(Method.PATCH.name(), url);
    }

    public static class MyHttpRequestHelper extends MyHttpRequest {

        @Override
        public MyHttpRequestHelper method(String method) {
            super.method(method);
            return this;
        }

        @Override
        public MyHttpRequestHelper headers(Map<String, Object> headers) {
            super.headers(headers);
            return this;
        }

        @Override
        public MyHttpRequestHelper requestUrl(String requestUrl) {
            super.requestUrl(requestUrl);
            return this;
        }

        @Override
        public MyHttpRequestHelper params(Map<String, String> params) {
            super.params(params);
            return this;
        }

        @Override
        public MyHttpRequestHelper jsonBody(Object jsonBody) {
            super.jsonBody(jsonBody);
            return this;
        }

        public MyHttpRequestHelper(String method, String requestUrl) {
            super(method, requestUrl);
        }

        public MyHttpResponse send() throws IOException {
            return send(null);
        }

        public MyHttpResponse send(Consumer<MyHttpRequest> consumer) throws IOException {
            final MyHttpClient instance = MyHttpClient.DEFAULT_INSTANCE.instance();
            return MyHttpClient.send(this, instance.closeableHttpClient, instance.webCacheHandler, MyHttpResponse.DEFAULT_HANDLER, consumer, instance.postProcess);
        }

        public MyHttpResponse sendBy(@NonNull MyHttpClient client) throws IOException {
            return client.send(this);
        }

        public MyHttpResponse sendBy() throws IOException {
            return MyHttpClient.DEFAULT_INSTANCE.instance().send(this);
        }

        public MyHttpResponse downloadBy(@NonNull MyHttpClient client, String savePath) throws IOException {
            return client.download(this, savePath);
        }

        public MyHttpResponse downloadBy(String savePath) throws IOException {
            return MyHttpClient.DEFAULT_INSTANCE.instance().download(this, savePath);
        }

    }

}