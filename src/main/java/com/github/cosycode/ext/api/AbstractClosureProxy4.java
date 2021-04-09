package com.github.cosycode.ext.api;

import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;


/**
 * <b>Description : </b> 抽象闭包代理类
 * <p>
 * <b>created in </b> 2021/4/5
 *
 * @param <T> 代理方法的函数式接口实例
 * @param <P> 代理方法可能传入的参数类型
 * @param <R> 代理方法可能的返回值类型
 * @author CPF
 * @since 1.0
 */
public abstract class AbstractClosureProxy4<T extends AbstractClosureProxy4.Functional, P, R> {

    public interface Functional {
    }

    @FunctionalInterface
    public interface IConsumer<T> extends Functional, java.util.function.Consumer<T> {
    }

    @FunctionalInterface
    public interface IFunction<T, R> extends Functional, java.util.function.Function<T, R> {
    }

    @FunctionalInterface
    public interface ISupplier<T> extends Functional, java.util.function.Supplier<T> {
    }

    @FunctionalInterface
    public interface IRunnable extends Functional, Runnable {
    }


    /**
     * 代理方法的函数式接口实例
     */
    protected final T functional;

    /**
     * 函数式接口 then 对传入参数的操作函数, 如果该项为 null, 则默认
     */
    protected final BiFunction<T, P, R> biFunction;

    protected AbstractClosureProxy4(@NonNull T functional) {
        this.functional = functional;
        biFunction = geneDefaultBiFunction(functional);
    }

    protected AbstractClosureProxy4(@NonNull T functional, @NonNull BiFunction<T, P, R> function) {
        this.functional = functional;
        this.biFunction = function;
    }

    protected AbstractClosureProxy4(@NonNull T functional, @NonNull BiConsumer<T, P> biConsumer) {
        this.functional = functional;
        this.biFunction = (t, p) -> {
            biConsumer.accept(t, p);
            return null;
        };
    }

    /**
     * 闭包代理方法: Function
     */
    public abstract R closureFunction(P params);

    /**
     * 闭包代理方法: Consumer
     */
    public void closureConsumer(P params) {
        closureFunction(params);
    }

    /**
     * 闭包代理方法: Supplier
     */
    public R closureSupplier() {
        return closureFunction(null);
    }

    /**
     * 闭包代理方法: Runnable
     */
    public void closureRunnable() {
        closureFunction(null);
    }

    /**
     * 根据 then 返回默认的闭包代理方法
     *
     * @return 默认的闭包代理方法
     */
    @SuppressWarnings("unchecked")
    public T proxy() {
        if (functional instanceof IConsumer) {
            final IConsumer<P> proxy = this::closureConsumer;
            return (T) proxy;
        } else if (functional instanceof IFunction) {
            final IFunction<P, R> proxy = this::closureFunction;
            return (T) proxy;
        } else if (functional instanceof ISupplier) {
            final ISupplier<R> proxy = this::closureSupplier;
            return (T) proxy;
        } else if (functional instanceof IRunnable) {
            final IRunnable proxy = this::closureRunnable;
            return (T) proxy;
        }
        throw new IllegalArgumentException("参数 funExpress" + functional + " 必须是支持的函数式接口");
    }

    /**
     * 根据函数式接口实例, 生成默认的针对(函数接口then调用的处理方式)的处理函数
     *
     * @param funExpress 函数式接口实例
     * @return 函数接口then调用的处理方式 的处理函数
     */
    @SuppressWarnings("unchecked")
    private BiFunction<T, P, R> geneDefaultBiFunction(T funExpress) {
        if (funExpress instanceof IConsumer) {
            return (t, p) -> {
                IConsumer<P> consumer = (IConsumer<P>) t;
                consumer.accept(p);
                return null;
            };
        } else if (funExpress instanceof IFunction) {
            return (t, p) -> {
                IFunction<P, R> consumer = (IFunction<P, R>) t;
                return consumer.apply(p);
            };
        } else if (funExpress instanceof ISupplier) {
            return (t, p) -> {
                ISupplier<R> supplier = ((ISupplier<R>) t);
                return supplier.get();
            };
        } else if (funExpress instanceof IRunnable) {
            return (t, p) -> {
                ((IRunnable) t).run();
                return null;
            };
        }
        throw new IllegalArgumentException("参数 funExpress" + funExpress + " 必须是支持的函数式接口");
    }

}
