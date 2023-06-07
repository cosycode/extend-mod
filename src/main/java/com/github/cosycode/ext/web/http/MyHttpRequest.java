package com.github.cosycode.ext.web.http;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.hc.core5.http.Method;

import java.util.Map;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/5/14
 * </p>
 *
 * @author CPF
 **/
@Data
@Accessors(fluent = true)
public class MyHttpRequest {

    private String method;
    private Map<String, Object> headers;
    private String requestUrl;
    private Map<String, String> params;
    private Object jsonBody;

    public MyHttpRequest(String method, String requestUrl) {
        this.method = method;
        this.requestUrl = requestUrl;
    }

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

}
