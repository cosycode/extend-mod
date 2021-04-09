package com.github.cosycode.ext.hub;

import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/7
 *
 * @author CPF
 * @since 1.0
 **/
public class AllClosureProxy <T, P, R> extends AbstractClosureProxy<T, P, R>{
    protected AllClosureProxy(@NonNull T then) {
        super(then);
    }

    protected AllClosureProxy(@NonNull T then, @NonNull BiFunction<T, P, R> function) {
        super(then, function);
    }

    protected AllClosureProxy(@NonNull T then, @NonNull BiConsumer<T, P> biConsumer) {
        super(then, biConsumer);
    }

    /**
     * 闭包代理方法: Function
     *
     * @param params
     */
    @Override
    public R closureFunction(P params) {
        return null;
    }

    public static class DebugProxy {
        private String sb;

    }

}
