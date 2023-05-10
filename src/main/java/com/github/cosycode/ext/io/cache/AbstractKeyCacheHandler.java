package com.github.cosycode.ext.io.cache;

import com.github.cosycode.common.base.SupplierWithThrow;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/12/8
 * </p>
 *
 * @author pengfchen
 * @since 0.2.2
 **/
@AllArgsConstructor
public abstract class AbstractKeyCacheHandler<P, R> {

    AbstractMapCacheHandler<R> mapCacheHandler;

    /**
     *
     * @param p key
     * @param supplier for getting cache.
     * @param validate judge whether the response is valid. save only if the response is valid.
     * @return response.
     */
    public R computeIfAbsent(P p, @NonNull SupplierWithThrow<R, IOException> supplier, Predicate<R> validate) throws IOException {
        String requestKey = "";
        boolean useCache = filter(p);
        if (useCache) {
            requestKey = computeKey(p);
            R response = get(requestKey);
            if (response != null) {
                return response;
            }
        }
        R response = supplier.get();
        if (useCache) {
            if (validate == null || validate.test(response)) {
                put(requestKey, response);
            }
        }
        return response;
    }

    /**
     * judge whether it should use cache, generally, it will use cache only for get method.
     */
    public abstract boolean filter(P p);

    public abstract String computeKey(P p);

    public R get(String key) {
        return mapCacheHandler.get(key);
    }

    public void put(String requestKey, R myHttpResponse) {
        mapCacheHandler.put(requestKey, myHttpResponse);
    }

}
