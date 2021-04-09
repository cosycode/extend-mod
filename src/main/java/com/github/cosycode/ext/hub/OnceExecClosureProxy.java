package com.github.cosycode.ext.hub;

import lombok.NonNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.*;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
 * @since 1.0
 **/
public class OnceExecClosureProxy<T, P, R> extends AbstractClosureProxy<T, P, R>{

    private final Lock lock = new ReentrantLock();

    private T skip;

    public OnceExecClosureProxy(@NonNull T then) {
        super(then);
    }

    public OnceExecClosureProxy(@NonNull T then, @NonNull BiFunction<T, P, R> function) {
        super(then, function);
    }

    public OnceExecClosureProxy(@NonNull T then, @NonNull BiConsumer<T, P> biConsumer) {
        super(then, biConsumer);
    }

    public OnceExecClosureProxy<T, P, R> skip(T skip) {
        this.skip = skip;
        return this;
    }

    @Override
    public R closureFunction(P params) {
        if (lock.tryLock()) {
            try {
                if (funExpress != null) {
                    return biFunction.apply(funExpress, params);
                }
            } finally {
                lock.unlock();
            }
        } else {
            if (skip != null) {
                return biFunction.apply(skip, params);
            }
        }
        return null;
    }

    public static <T> T of(T then) {
        return new OnceExecClosureProxy<>(then).proxy();
    }

    public static <T, P, R> T of(T then, BiFunction<T, P, R> function) {
        return new OnceExecClosureProxy<>(then, function).proxy();
    }

    public static <T, P> T of(T then, BiConsumer<T, P> biConsumer) {
        return new OnceExecClosureProxy<>(then, biConsumer).proxy();
    }

    public static <T> T of(T then, T skip) {
        return new OnceExecClosureProxy<>(then).skip(skip).proxy();
    }

    public static <T, P, R> T of(T then, T skip, BiFunction<T, P, R> function) {
        return new OnceExecClosureProxy<>(then, function).skip(skip).proxy();
    }

    public static <T, P> T of(T then, T skip, BiConsumer<T, P> biConsumer) {
        return new OnceExecClosureProxy<>(then, biConsumer).skip(skip).proxy();
    }

}
