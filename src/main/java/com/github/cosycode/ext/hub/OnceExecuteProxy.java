package com.github.cosycode.ext.hub;

import lombok.NonNull;
import lombok.Setter;

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
public class OnceExecuteProxy<T, P, R> extends AbstractClosureProxy<T, P, R>{

    private final Lock lock = new ReentrantLock();

    @Setter
    private T skip;

    public OnceExecuteProxy(T then) {
        super(then);
    }

    public OnceExecuteProxy(T then, BiFunction<T, P, R> function) {
        super(then, function);
    }

    public OnceExecuteProxy(T then, T skip, BiFunction<T, P, R> function) {
        super(then, function);
        this.skip = skip;
    }

    @Override
    public R closureFunction(P params) {
        if (lock.tryLock()) {
            try {
                if (then != null) {
                    return biFunction.apply(then, params);
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


    public static <T, P, R> Function<P, R> convert(@NonNull T then, T skip, @NonNull BiFunction<T, P, R> function) {
        OnceExecuteProxy<T, P, R> onceExecutor = new OnceExecuteProxy<>(then, skip, function);
        return onceExecutor::closureFunction;
    }

    public static <T, P, R> Function<P, R> convert(@NonNull T then, @NonNull BiFunction<T, P, R> function) {
        OnceExecuteProxy<T, P, R> onceExecutor = new OnceExecuteProxy<>(then, function);
        return onceExecutor::closureFunction;
    }

    public static <T, P> Consumer<P> convert(@NonNull T then, T skip, @NonNull BiConsumer<T, P> biConsumer) {
        BiFunction<T, P, ?> biFunction = (t, p) -> {
            biConsumer.accept(t, p);
            return null;
        };
        OnceExecuteProxy<T, P, ?> onceExecutor = new OnceExecuteProxy<>(then, skip, biFunction);
        return onceExecutor::closureFunction;
    }

    public static <T, P> Consumer<P> convert(@NonNull T then, @NonNull BiConsumer<T, P> biConsumer) {
        BiFunction<T, P, ?> biFunction = (t, p) -> {
            biConsumer.accept(t, p);
            return null;
        };
        OnceExecuteProxy<T, P, ?> onceExecutor = new OnceExecuteProxy<>(then, biFunction);
        return onceExecutor::closureFunction;
    }

}
