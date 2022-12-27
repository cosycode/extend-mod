package com.github.cosycode.ext.web.http;


import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.util.otr.PrintTool;
import com.github.cosycode.common.validate.RequireUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpUtils {

    public static void jsonHeader(@NonNull Map<String, Object> header) {
        header.computeIfAbsent(HttpHeaders.CONTENT_TYPE, k -> ContentType.APPLICATION_JSON);
        header.computeIfAbsent(HttpHeaders.ACCEPT_ENCODING, k -> "gzip, x-gzip, deflate");
        header.computeIfAbsent(HttpHeaders.CONNECTION, k -> "keep-alive");
    }

    public static Map<String, Object> jsonHeader() {
        Map<String, Object> header = new HashMap<>();
        jsonHeader(header);
        return header;
    }

    public static String get(String urlString, Map<String, Object> headers, Map<String, String> params) throws IOException {
        List<Header> headerList = new ArrayList<>();
        if (headers != null) {
            // Map<String, Object> headers 转换为 List<Header>
            for (Map.Entry<String, Object> objectEntry : headers.entrySet()) {
                headerList.add(new BasicHeader(objectEntry.getKey(), objectEntry.getValue()));
            }
        }
        // Map<String, String> params 转换为 List<BasicNameValuePair> param
        List<NameValuePair> list = new ArrayList<>();
        if (params != null) {
            for (Map.Entry<String, String> stringEntry : params.entrySet()) {
                list.add(new BasicNameValuePair(stringEntry.getKey(), stringEntry.getValue()));
            }
        }
        // 拼接 Uri
        URL url = new URL(urlString);
        URIBuilder uriBuilder = new URIBuilder().setScheme(url.getProtocol()).setHost(url.getHost()).setPort(url.getPort()).setPath(url.getPath()).setParameters(list);
        URI uri;
        try {
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // 创建 GET 请求对象
        HttpGet httpGet = new HttpGet(uri);
        // 设置 header
        if (headers != null && ! headers.isEmpty()) {
            for (Map.Entry<String, Object> objectEntry : headers.entrySet()) {
                httpGet.addHeader(new BasicHeader(objectEntry.getKey(), objectEntry.getValue()));
            }
        }
        // 调用 HttpClient 的 execute 方法执行请求
        CloseableHttpResponse response = Http5Client.getCloseableHttpClient().execute(httpGet);
        // 验证请求状态
        int code = response.getCode();
        RequireUtil.requireBooleanTrue(HttpStatus.SC_OK == code, PrintTool.format("请求失败, 状态是{}, query: ", code, uri.getQuery()));
        // 转换结果
        HttpEntity entity = response.getEntity();
        return Throws.fun(entity, EntityUtils::toString).logThrowable("请求转换失败" + uri.getQuery()).value();
    }

    public static String post(String urlString, Map<String, Object> headers, Map<String, String> params, String jsonBody) throws IOException {
        // Map<String, String> params 转换为 List<BasicNameValuePair> param
        List<NameValuePair> list = new ArrayList<>();
        if (params != null) {
            for (Map.Entry<String, String> stringEntry : params.entrySet()) {
                list.add(new BasicNameValuePair(stringEntry.getKey(), stringEntry.getValue()));
            }
        }
        // 拼接 Uri
        URL url = new URL(urlString);
        URIBuilder uriBuilder = new URIBuilder().setScheme(url.getProtocol()).setHost(url.getHost()).setPort(url.getPort()).setPath(url.getPath()).setParameters(list);
        URI uri;
        try {
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // 创建 POST 请求对象
        HttpPost httpPost = new HttpPost(uri);
        // 设置 header
        if (headers != null && ! headers.isEmpty()) {
            for (Map.Entry<String, Object> objectEntry : headers.entrySet()) {
                httpPost.addHeader(new BasicHeader(objectEntry.getKey(), objectEntry.getValue()));
            }
        }
        // 设置 body
        if (jsonBody != null) {
            httpPost.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        }
        // 调用 HttpClient 的 execute 方法执行请求
        CloseableHttpResponse response = Http5Client.getCloseableHttpClient().execute(httpPost);
        // 验证请求状态
        int code = response.getCode();
        RequireUtil.requireBooleanTrue(HttpStatus.SC_OK == code, PrintTool.format("请求失败, 状态是{}, query: ", code, uri.getQuery()));
        // 转换结果
        HttpEntity entity = response.getEntity();
        return Throws.fun(entity, EntityUtils::toString).logThrowable("请求转换失败" + uri.getQuery()).value();
    }

}