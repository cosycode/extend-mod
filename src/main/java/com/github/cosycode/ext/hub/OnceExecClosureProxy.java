package com.github.cosycode.ext.hub;

import lombok.NonNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
 **/
public class OnceExecClosureProxy<T, P, R> extends AbstractClosureProxy<T, P, R> {

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

    public static <T> T of(T then) {
        return new OnceExecClosureProxy<>(then).proxy();
    }

    public OnceExecClosureProxy<T, P, R> skip(T skip) {
        this.skip = skip;
        return this;
    }

    @Override
    public R closureFunction(P params) {
        if (lock.tryLock()) {
            try {
                if (functional != null) {
                    return biFunction.apply(functional, params);
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

}
