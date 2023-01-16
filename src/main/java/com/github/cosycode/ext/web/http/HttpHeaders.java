package com.github.cosycode.ext.web.http;

import lombok.NonNull;
import org.apache.hc.core5.http.ContentType;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/1/16
 * </p>
 *
 * @author pengfchen
 * @since
 **/
public class HttpHeaders {

    public static void jsonHeader(@NonNull Map<String, Object> header) {
        header.computeIfAbsent(org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE, k -> ContentType.APPLICATION_JSON);
        header.computeIfAbsent(org.apache.hc.core5.http.HttpHeaders.ACCEPT_ENCODING, k -> "gzip, x-gzip, deflate");
        header.computeIfAbsent(org.apache.hc.core5.http.HttpHeaders.CONNECTION, k -> "keep-alive");
    }

    public static Map<String, Object> jsonHeader() {
        Map<String, Object> header = new HashMap<>();
        jsonHeader(header);
        return header;
    }


}
