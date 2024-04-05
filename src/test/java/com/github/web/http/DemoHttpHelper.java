package com.github.web.http;

import com.github.cosycode.ext.web.http.MyHttpClient;
import com.github.cosycode.ext.web.http.MyHttpEntityResponse;
import com.github.cosycode.ext.web.http.MyHttpRequest;
import com.github.cosycode.ext.web.http.MyHttpResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DemoHttpHelper<T> {

    private final MyHttpRequest myHttpRequest;

    private final Class<T> clazz;

    public static DemoKindBuilder builder(String baseUrl) {
        return new DemoKindBuilder(baseUrl);
    }

    @Getter
    @AllArgsConstructor
    enum DEMO_KIND {
        ENTITY1("value1"),
        ENTITY2("value2");

        private final String element;

    }

    public static class DemoKindBuilder {

        private final String baseUrl;

        private DemoKindBuilder(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public RequestBuilder<DemoEntity1> demoEntity1() {
            return new RequestBuilder<>(baseUrl, DEMO_KIND.ENTITY1, DemoEntity1.class);
        }

        public RequestBuilder<DemoEntity2> demoEntity2() {
            return new RequestBuilder<>(baseUrl, DEMO_KIND.ENTITY2, DemoEntity2.class);
        }
    }

    @AllArgsConstructor
    public static class RequestBuilder<R> {

        private final String baseUrl;

        private final DEMO_KIND demoKind;

        private final Class<R> returnClazz;

        public DemoHttpHelper<R> buildGet() {
            return new DemoHttpHelper<>(MyHttpRequest.buildGet(baseUrl), returnClazz);
        }

        public DemoHttpHelper<R> buildPost() {
            return new DemoHttpHelper<>(MyHttpRequest.buildPost(baseUrl), returnClazz);
        }

        public DemoHttpHelper<R> buildPut() {
            return new DemoHttpHelper<>(MyHttpRequest.buildPut(baseUrl), returnClazz);
        }

        public DemoHttpHelper<R> buildDelete() {
            return new DemoHttpHelper<>(MyHttpRequest.buildDelete(baseUrl), returnClazz);
        }

        public DemoHttpHelper<R> buildGetList() {
            return new DemoHttpHelper<>(MyHttpRequest.buildGet(baseUrl), returnClazz);
        }

        public MyHttpEntityResponse<R> get() throws IOException {
            MyHttpRequest myHttpRequest = MyHttpRequest.buildGet(baseUrl);
            MyHttpResponse resp = MyHttpClient.DEFAULT_INSTANCE.instance().send(myHttpRequest);
            return MyHttpEntityResponse.newWithJsonParser(resp.code(), resp.data(), returnClazz);
        }

    }

    @Test
    @Ignore
    public void UseCaseTest() throws IOException {
        DemoEntity1 entity1 = DemoHttpHelper.builder("cluster1").demoEntity1().get().toEntity();
        System.out.println(entity1);
        DemoEntity2 entity2 = DemoHttpHelper.builder("cluster2").demoEntity2().get().toEntity();
        System.out.println(entity2);
    }

}
