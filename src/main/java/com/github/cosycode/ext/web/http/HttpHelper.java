package com.github.cosycode.ext.web.http;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Method;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * <b>Description : </b> http 调用的 工具类
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

        public MyHttpRequestHelper(String method, String requestUrl) {
            super(method, requestUrl);
        }

        /**
         * 发送请求之前事项
         */
        public MyHttpResponse send(@NonNull Consumer<MyHttpRequest> consumer) throws IOException {
            return MyHttpClient.send(this, Http5ClientConfig.getCloseableHttpClient(), null, MyHttpResponse.DEFAULT_HANDLER, consumer, null);
        }

        /**
         * 发送请求之前事项
         */
        public MyHttpResponse sendBy(@NonNull MyHttpClient client) throws IOException {
            return client.send(this);
        }

        /**
         * 发送请求之前事项
         */
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