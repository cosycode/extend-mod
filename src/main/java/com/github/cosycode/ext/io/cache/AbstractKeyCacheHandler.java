package com.github.cosycode.ext.io.cache;

import com.github.cosycode.common.base.SupplierWithThrow;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.IOException;

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

    public R computeIfAbsent(P p, @NonNull SupplierWithThrow<R, IOException> supplier) throws IOException {
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
            put(requestKey, response);
        }
        return response;
    }

    public abstract boolean filter(P p);

    public abstract String computeKey(P p);

    public R get(String key) {
        return mapCacheHandler.get(key);
    }

    public void put(String requestKey, R myHttpResponse) {
        mapCacheHandler.put(requestKey, myHttpResponse);
    }

}
