package com.github.cosycode.ext.web.http;

import com.github.cosycode.common.lang.BaseRuntimeException;
import com.github.cosycode.ext.se.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.util.function.Function;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/1/16
 * </p>
 **/
public class MyHttpEntityResponse<T> extends MyHttpResponse {

    private final String convertType;

    private final Object convertObject;

    public static <R> MyHttpEntityResponse<R> newWithJsonParser(int code, String data, Class<?> clazz) {
        return new MyHttpEntityResponse<>(code, data, "json", clazz);
    }

    public static <R> MyHttpEntityResponse<R> newWithCustomParser(int code, String data, Function<MyHttpResponse, R> function) {
        return new MyHttpEntityResponse<>(code, data, "custom", function);
    }

    private MyHttpEntityResponse(int code, String data, String convertType, Object convertObject) {
        super(code, data);
        this.convertType = convertType;
        this.convertObject = convertObject;
    }

    @SuppressWarnings("unchecked")
    public T toEntity() {
        if (isCode(404)) {
            return null;
        }
        if (! isSuccess() || StringUtils.isBlank(data())) {
            throw new BaseRuntimeException("can't convert to Entity, code or data is abnormal ==> %s", JsonUtils.toJson(this));
        }
        switch (convertType) {
            case "json": {
                Class<T> tClass = (Class<T>) convertObject;
                return JsonUtils.fromJson(data(), tClass);
            }
            case "custom": {
                Function<MyHttpResponse, T> convertFunction = (Function<MyHttpResponse, T>) convertObject;
                return convertFunction.apply(this);
            }
            default:
                throw new BaseRuntimeException("can't convert, not support the type: %s, ==> %s", convertType, this);
        }
    }

    static <T> HttpClientResponseHandler<MyHttpEntityResponse<T>> getReasonPhrase(Class<T> tClass) {
        return response -> {
            int responseCode = response.getCode();
            final String responseData;
            HttpEntity entity = response.getEntity();
            if ((responseCode < 500 && responseCode >= 200)) {
                if (entity == null) {
                    responseData = null;
                } else {
                    try {
                        responseData = EntityUtils.toString(entity);
                    } catch (ParseException var3) {
                        throw new ClientProtocolException(var3);
                    }
                }
                return MyHttpEntityResponse.newWithJsonParser(responseCode, responseData, tClass);
            } else {
                try {
                    responseData = EntityUtils.toString(entity);
                    return MyHttpEntityResponse.newWithJsonParser(responseCode, responseData, tClass);
                } catch (ParseException var3) {
                    EntityUtils.consume(entity);
                    throw new HttpResponseException(responseCode, response.getReasonPhrase());
                }
            }
        };
    }

}