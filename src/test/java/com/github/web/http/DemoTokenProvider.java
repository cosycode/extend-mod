package com.github.web.http;

import com.github.cosycode.common.ext.hub.LazySingleton;
import com.github.cosycode.ext.io.cache.AbstractObjCacheHandler;
import com.github.cosycode.ext.io.cache.ICacheStack;
import com.github.cosycode.ext.io.cache.ObjCacheChain;
import com.github.cosycode.ext.web.http.HttpHeaderHelper;
import com.github.cosycode.ext.web.http.HttpUtils;
import com.github.cosycode.ext.web.http.MyHttpResponse;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/12/7
 * </p>
 *
 * @author pengfchen
 **/
@Slf4j
public class DemoTokenProvider {

    public static final LazySingleton<ObjCacheChain<DemoClientToken>> DemoTokenSingleton = LazySingleton.of(() -> {
        List<AbstractObjCacheHandler<DemoClientToken>> myLinkedList = new LinkedList<>();
        // get/set token from memory
        myLinkedList.add(AbstractObjCacheHandler.geneMemoryCacheHandler("DemoToken from memory"));
        // get/set token from file
        myLinkedList.add(AbstractObjCacheHandler.geneFileCacheHandler("DemoToken from file", "cache/token/Demo-token.json", DemoClientToken.class));
        // get/set token from web
        myLinkedList.add(new AbstractObjCacheHandler<DemoClientToken>("DemoToken from web") {
            @Override
            public void put(DemoClientToken clientToken) {
            }

            @Override
            public DemoClientToken get() {
                String body = "{\"Credentials\":{\"username\": \"cpf\",\"password\":\"xxxxx@123\"}}";
                final String urlString = "https://toekn-identity.service.com/tokens";
                Map<String, Object> headers = HttpHeaderHelper.jsonHeader();
                // call api
                MyHttpResponse post;
                try {
                    post = HttpUtils.post(urlString, headers, null, body);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // parse response
                DocumentContext jsonDocument = JsonPath.parse(post.data());
                String expires = jsonDocument.read("$.access.token.expires", String.class);
                String token = jsonDocument.read("$.access.token.id", String.class);
                LocalDateTime parse = LocalDateTime.parse(expires.substring(0, expires.length() - 1)).plusHours(8);
                DemoClientToken clientToken = new DemoClientToken();
                clientToken.setToken(token);
                clientToken.setExpires(parse);
                log.info("get Demo token success, token: {}, expires: {}", token, parse);
                return clientToken;
            }
        });
        return new ObjCacheChain<>(myLinkedList);
    });

    /**
     * used for save token
     */
    @Data
    public static class DemoClientToken implements ICacheStack {

        String token;
        String expires;

        @Override
        public boolean validate() {
            if (this.token != null && this.expires != null) {
                // It is considered expired when it is close to the expiration time within 30 minutes.
                return getExpires().isAfter(LocalDateTime.now().minusMinutes(30));
            }
            return false;
        }

        public void setExpires(LocalDateTime parse) {
            this.expires = parse.toString();
        }

        public LocalDateTime getExpires() {
            return LocalDateTime.parse(expires);
        }
    }

}