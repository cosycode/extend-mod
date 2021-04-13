package com.github.cosycode.ext.hub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/12
 *
 * @author CPF
 * @since 1.0
 **/
public class CacheClosureProxy<P, R> extends AbstractClosureProxy<Function<P,R>, P, R> {

    private final Map<P, R> cache = new ConcurrentHashMap<>();

    public CacheClosureProxy(Function<P,R> functional) {
        super(functional);
    }

    @Override
    public R closureFunction(P params) {
        return cache.computeIfAbsent(params, p -> biFunction.apply(functional, p));
    }

}
