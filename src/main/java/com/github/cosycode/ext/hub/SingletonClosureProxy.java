package com.github.cosycode.ext.hub;

import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * <b>Description : </b> 闭包代理单例模式:
 * <p>
 * 作用是 将一个原本不是单例的方法 在经过代理后变成单例方法.
 * </p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
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

    public static <R> Supplier<R> of(Supplier<R> supplier) {
        return new SingletonClosureProxy<>(supplier).proxy();
    }

    @Override
    public R closureFunction(P params) {
        if (obj == null) {
            synchronized (this) {
                if (obj == null) {
                    final R apply = biFunction.apply(functional, params);
                    obj = apply;
                    return apply;
                }
            }
        }
        return obj;
    }

}
