package com.github.cosycode.ext.web.http;

import lombok.Getter;
import lombok.NonNull;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/1/16
 * </p>
 *
 * @author pengfchen
 * @since 1.0
 **/
public class HttpHeaderHelper {

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


    public static HttpHeaderBuilder build() {
        return new HttpHeaderBuilder();
    }

    public static class HttpHeaderBuilder {

        @Getter
        Map<String, Object> headers = new HashMap<>();

        public Accept accept() {
            return new Accept();
        }

        public ContentType contentType() {
            return new ContentType();
        }

        public CacheControl cacheControl() {
            return new CacheControl();
        }

        public class Accept {
            public HttpHeaderBuilder application$Json() {
                HttpHeaderBuilder.this.headers.put("accept", "application/json");
                return HttpHeaderBuilder.this;
            }
        }

        public class ContentType {
            public HttpHeaderBuilder application$Json() {
                HttpHeaderBuilder.this.headers.put("content-type", "application/json");
                return HttpHeaderBuilder.this;
            }
        }

        public class CacheControl {
            public HttpHeaderBuilder no$cache() {
                HttpHeaderBuilder.this.headers.put("cache-control", "no-cache");
                return HttpHeaderBuilder.this;
            }
        }
    }

}
