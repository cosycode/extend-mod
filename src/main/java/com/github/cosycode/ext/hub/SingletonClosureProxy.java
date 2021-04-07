package com.github.cosycode.ext.hub;

import com.github.cosycode.ext.hub.AbstractClosureProxy;
import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * <b>Description : </b> 闭包代理单例模式
 * <p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
 * @since 1.0
 **/
public class SingletonClosureProxy<T, P, R> extends AbstractClosureProxy<T, P, R> {

    /**
     * 单例模式仅仅保证引用可见性即可
     */
    @SuppressWarnings("java:S3077")
    private volatile R obj;

    public SingletonClosureProxy(@NonNull T then) {
        super(then);
    }

    public SingletonClosureProxy(@NonNull T then, @NonNull BiFunction<T, P, R> function) {
        super(then, function);
    }

    public SingletonClosureProxy(@NonNull T then, @NonNull BiConsumer<T, P> biConsumer) {
        super(then, biConsumer);
    }

    @Override
    public R closureFunction(P params) {
        if (obj == null) {
            synchronized (this) {
                if (obj == null) {
                    return biFunction.apply(then, params);
                }
            }
        }
        return obj;
    }
}
