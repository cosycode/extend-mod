package com.github.cosycode.ext.web.http;

import com.github.cosycode.ext.se.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HttpUtils {

    private static String createRequestUrl(String url, Map<String, String> paramMap) {
        // Map<String, String> params 转换为 List<BasicNameValuePair> param
        if (paramMap != null && ! paramMap.isEmpty()) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (Map.Entry<String, String> stringEntry : paramMap.entrySet()) {
                paramList.add(new BasicNameValuePair(stringEntry.getKey(), stringEntry.getValue()));
            }
            String paramString = paramList.stream().map(it -> it.getName() + "=" + it.getValue()).collect(Collectors.joining("&"));
            if (url.contains("?")) {
                return url + "&" + paramString;
            } else {
                return url + "?" + paramString;
            }
        } else {
            return url;
        }
    }

    public static <T> T http(String method, String requestUrl, Map<String, Object> headers, Map<String, String> params,
                             Object jsonBody, HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        // 拼接 url
        String url = createRequestUrl(requestUrl, params);
        // 创建 HttpUriRequestBase 对象
        HttpUriRequestBase httpUriRequestBase = new HttpUriRequestBase(method, URI.create(url));
        // Map<String, Object> headers 转换为 List<Header>
        if (headers != null && ! headers.isEmpty()) {
            for (Map.Entry<String, Object> objectEntry : headers.entrySet()) {
                httpUriRequestBase.addHeader(new BasicHeader(objectEntry.getKey(), objectEntry.getValue()));
            }
        }
        // 设置 body
        if (jsonBody != null) {
            String string = jsonBody instanceof String ? (String) jsonBody : JsonUtils.toJson(jsonBody);
            httpUriRequestBase.setEntity(new StringEntity(string, StandardCharsets.UTF_8));
        }
        // 调用 HttpClient 的 execute 方法执行请求
        return Http5Client.getCloseableHttpClient().execute(httpUriRequestBase, responseHandler);
    }

    public static MyHttpResponse get(String urlString, Map<String, Object> headers, Map<String, String> params) throws IOException {
        return http(HttpGet.METHOD_NAME, urlString, headers, params, null, MyHttpResponse.DEFAULT_HANDLER);
    }

    public static MyHttpResponse post(String urlString, Map<String, Object> headers, Map<String, String> params, Object jsonBody) throws IOException {
        return http(HttpPost.METHOD_NAME, urlString, headers, params, jsonBody, MyHttpResponse.DEFAULT_HANDLER);
    }

    public static MyHttpResponse put(String urlString, Map<String, Object> headers, Map<String, String> params, Object jsonBody) throws IOException {
        return http(HttpPut.METHOD_NAME, urlString, headers, params, jsonBody, MyHttpResponse.DEFAULT_HANDLER);
    }

    public static MyHttpResponse delete(String urlString, Map<String, Object> headers, Map<String, String> params, Object jsonBody) throws IOException {
        return http(HttpDelete.METHOD_NAME, urlString, headers, params, jsonBody, MyHttpResponse.DEFAULT_HANDLER);
    }

    @Contract(threading = ThreadingBehavior.STATELESS)
    public static class MyStringHttpClientResponseHandler implements HttpClientResponseHandler<String> {
        public String handleResponse(ClassicHttpResponse response) throws IOException {
            HttpEntity entity = response.getEntity();
            if (response.getCode() >= 300) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(response.getCode(), response.getReasonPhrase());
            } else {
                return entity == null ? null : this.handleEntity(entity);
            }
        }

        public String handleEntity(HttpEntity entity) throws IOException {
            try {
                return EntityUtils.toString(entity);
            } catch (ParseException var3) {
                throw new ClientProtocolException(var3);
            }
        }
    }

}