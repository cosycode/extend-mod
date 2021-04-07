package com.github.cosycode.ext.hub;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @date 2020/12/10 15:11
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OnceExecutes {

    public static class OnceExecutorForCommon<T, P, R> {

        private final Lock lock = new ReentrantLock();

        private final T then;

        @Setter
        private T skip;

        private final BiFunction<T, P, R> biFunction;

        public OnceExecutorForCommon(T then, BiFunction<T, P, R> function) {
            this.then = then;
            this.biFunction = function;
        }

        public OnceExecutorForCommon(T then, T skip, BiFunction<T, P, R> function) {
            this.then = then;
            this.skip = skip;
            this.biFunction = function;
        }

        public R onceExe(P params) {
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

    }

    public static class OnceExecutorForRunnable {

        private final Lock lock = new ReentrantLock();

        private final Runnable then;

        @Setter
        private Runnable skip;

        public OnceExecutorForRunnable(Runnable then) {
            this.then = then;
        }

        public void onceExe() {
            if (lock.tryLock()) {
                try {
                    if (then != null) {
                        then.run();
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                if (skip != null) {
                    skip.run();
                }
            }
        }
    }

    public static class OnceExecutorForConsumer<T> {

        private final Lock lock = new ReentrantLock();

        private final Consumer<T> then;

        @Setter
        private Consumer<T> skip;

        public OnceExecutorForConsumer(Consumer<T> then) {
            this.then = then;
        }

        public void onceExe(T e) {
            if (lock.tryLock()) {
                try {
                    if (then != null) {
                        then.accept(e);
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                if (skip != null) {
                    skip.accept(e);
                }
            }
        }
    }

    public static class OnceExecutorForSupplier<T> {

        private final Lock lock = new ReentrantLock();

        private final Supplier<T> then;

        @Setter
        private Supplier<T> skip;

        public OnceExecutorForSupplier(Supplier<T> then) {
            this.then = then;
        }

        public T onceExe() {
            if (lock.tryLock()) {
                try {
                    return then.get();
                } finally {
                    lock.unlock();
                }
            } else {
                if (skip != null) {
                    return skip.get();
                }
            }
            return null;
        }
    }

    public static class OnceExecutorForFunction<T, R> {

        private final Lock lock = new ReentrantLock();

        private final Function<T, R> then;

        @Setter
        private Function<T, R> skip;

        public OnceExecutorForFunction(Function<T, R> then) {
            this.then = then;
        }

        public R onceExe(T e) {
            if (lock.tryLock()) {
                try {
                    return then.apply(e);
                } finally {
                    lock.unlock();
                }
            } else {
                if (skip != null) {
                    return skip.apply(e);
                }
            }
            return null;
        }
    }

    public static <T> Consumer<T>  consumer(@NonNull Consumer<T> then) {
        OnceExecutorForConsumer<T> onceExecutor = new OnceExecutorForConsumer<>(then);
        return onceExecutor::onceExe;
    }

    public static <T> Consumer<T> consumer(@NonNull Consumer<T> then, Consumer<T> skip) {
        OnceExecutorForConsumer<T> onceExecutor = new OnceExecutorForConsumer<>(then);
        onceExecutor.setSkip(skip);
        return onceExecutor::onceExe;
    }

    public static Runnable runnable(@NonNull Runnable then) {
        OnceExecutorForRunnable onceExecutor = new OnceExecutorForRunnable(then);
        return onceExecutor::onceExe;
    }

    public static Runnable runnable(@NonNull Runnable then, Runnable skip) {
        OnceExecutorForRunnable onceExecutor = new OnceExecutorForRunnable(then);
        onceExecutor.setSkip(skip);
        return onceExecutor::onceExe;
    }

    public static <T, R> Function<T, R> function(@NonNull Function<T, R> then) {
        OnceExecutorForFunction<T, R> onceExecutor = new OnceExecutorForFunction<>(then);
        return onceExecutor::onceExe;
    }

    public static <T, R> Function<T, R> function(@NonNull Function<T, R> then, Function<T, R> skip) {
        OnceExecutorForFunction<T, R> onceExecutor = new OnceExecutorForFunction<>(then);
        onceExecutor.setSkip(skip);
        return onceExecutor::onceExe;
    }

    public static <R> Supplier<R> supplier(@NonNull Supplier<R> then) {
        OnceExecutorForSupplier<R> onceExecutor = new OnceExecutorForSupplier<>(then);
        return onceExecutor::onceExe;
    }

    public static <R> Supplier<R> supplier(@NonNull Supplier<R> then, Supplier<R> skip) {
        OnceExecutorForSupplier<R> onceExecutor = new OnceExecutorForSupplier<>(then);
        onceExecutor.setSkip(skip);
        return onceExecutor::onceExe;
    }

    public static <T, P, R> Function<P, R> exec(@NonNull T then, @NonNull BiFunction<T, P, R> function) {
        OnceExecutorForCommon<T, P, R> onceExecutor = new OnceExecutorForCommon<>(then, null, function);
        return onceExecutor::onceExe;
    }

    public static <T, P, R> Function<P, R> exec(@NonNull T then, T skip, @NonNull BiFunction<T, P, R> function) {
        OnceExecutorForCommon<T, P, R> onceExecutor = new OnceExecutorForCommon<>(then, skip, function);
        return onceExecutor::onceExe;
    }

}
