package com.github.web.http;

import com.github.cosycode.common.lang.NotSupportException;
import com.github.cosycode.ext.se.util.JsonUtils;
import com.github.cosycode.ext.web.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DemoRestClient {

    private static final String DEMO_ENDPOINT = "https://demo.com";
    private static final MyHttpClient httpClient = new MyHttpClient(Http5ClientConfig.getCloseableHttpClient(), MyHttpResponse.DEFAULT_HANDLER).setPreProcess(myHttpRequest -> {
        {
            Map<String, Object> headers = myHttpRequest.headers();
            headers = Optional.ofNullable(headers).orElse(new HashMap<>(8));
            headers.computeIfAbsent("Authentication", k -> DemoTokenProvider.DemoTokenSingleton.instance().getData().getToken());
            headers.computeIfAbsent(HttpHeaders.CONTENT_TYPE, k -> ContentType.APPLICATION_JSON);
            myHttpRequest.headers(headers);
        }
        {
            String requestUrl = myHttpRequest.requestUrl();
            if (!requestUrl.startsWith(DEMO_ENDPOINT)) {
                if (requestUrl.startsWith("/")) {
                    requestUrl = DEMO_ENDPOINT + requestUrl;
                } else {
                    requestUrl = DEMO_ENDPOINT + "/" + requestUrl;
                }
            }
            myHttpRequest.requestUrl(requestUrl);
        }
    });


    public DemoEntity1 getDemoEntity1(final String param1, final String param2) {
        String subUrl = String.format("/v1/param1/%s/param1/%s", param1, param2);
        return MyHttpTransaction.generate(httpClient, MyHttpRequest.buildGet(subUrl)).sendWithRuntimeException((t, resp) -> {
            if (resp.isCode(200)) {
                return JsonUtils.fromJson(resp.data(), DemoEntity1.class);
            }
            if (resp.isCode(404)) {
                return null;
            }
            throw new NotSupportException(JsonUtils.toJson(t));
        });
    }

    public DemoEntity1 getDemoEntity2(final String param1, final String param2) {
        String subUrl = String.format("/v1/param1/%s/param1/%s", param1, param2);
        return MyHttpTransaction.generate(httpClient, MyHttpRequest.buildGet(subUrl)).sendWithRuntimeException((t, resp) -> {
            if (resp.isCode(200)) {
                return JsonUtils.fromJson(resp.data(), DemoEntity1.class);
            }
            if (resp.isCode(404)) {
                return null;
            }
            throw new NotSupportException(JsonUtils.toJson(t));
        });
    }
}