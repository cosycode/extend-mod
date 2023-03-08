package com.github.cosycode.ext.web.http;

import com.github.cosycode.common.lang.BaseRuntimeException;
import com.github.cosycode.ext.se.json.JsonHelper;
import com.github.cosycode.ext.se.json.JsonNode;
import com.github.cosycode.ext.se.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/1/16
 * </p>
 *
 * @author pengfchen
 **/
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
@ToString
public class MyHttpResponse {

    public static final HttpClientResponseHandler<MyHttpResponse> DEFAULT_HANDLER = response -> {
        HttpEntity entity = response.getEntity();
        int responseCode = response.getCode();
        if (responseCode >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(responseCode, response.getReasonPhrase());
        } else {
            final String responseData;
            if (entity == null) {
                responseData = null;
            } else {
                try {
                    responseData = EntityUtils.toString(entity);
                } catch (ParseException var3) {
                    throw new ClientProtocolException(var3);
                }
            }
            return new MyHttpResponse(responseCode, responseData);
        }
    };

    private final int code;

    private final String data;

    public <T> T jsonParse(Class<T> tClass) {
        if (!isSuccess() || StringUtils.isBlank(data)) {
            throw new BaseRuntimeException("can't convert to {}, ==> {}", tClass.getName(), this);
        }
        return JsonUtils.fromJson(data, tClass);
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public JsonNode toJsonNode() {
        return JsonHelper.parse(data);
    }

}
