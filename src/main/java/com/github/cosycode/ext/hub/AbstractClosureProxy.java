package com.github.cosycode.ext.hub;

import java.util.Objects;
import java.util.function.*;

/**
 * <b>Description : </b> 抽象闭包代理类
 * <p>
 * <b>created in </b> 2021/4/5
 *
 * @author CPF
 * @since 1.0
 *
 * @param <T> 代理方法的函数式接口实例
 * @param <P> 代理方法可能传入的参数类型
 * @param <R> 代理方法可能的返回值类型
 */
public abstract class AbstractClosureProxy<T, P, R> {

    /**
     * 代理方法的函数式接口实例
     */
    protected final T functional;

    /**
     * 函数式接口 then 对传入参数的操作函数, 如果该项为 null, 则默认
     */
    protected final BiFunction<T, P, R> biFunction;

    protected AbstractClosureProxy(T functional) {
        this.functional = functional;
        this.biFunction = geneDefaultBiFunction();
    }

    protected AbstractClosureProxy(T functional, BiConsumer<T, P> biConsumer) {
        this(functional, (t, p) -> {
            biConsumer.accept(t, p);
            return null;
        });
    }

    protected AbstractClosureProxy(T functional, BiFunction<T, P, R> biFunction) {
        Objects.requireNonNull(functional, "functional 不能为 null");
        this.functional = functional;
        if (biFunction == null) {
            this.biFunction = geneDefaultBiFunction();
        } else {
            this.biFunction = biFunction;
        }
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

    @SuppressWarnings("unchecked")
    public P closureUnaryOperator(P params) {
        return (P) closureFunction(params);
    }

    /**
     * 根据 then 返回默认的闭包代理方法
     *
     * @return 默认的闭包代理方法
     */
    @SuppressWarnings("unchecked")
    public T proxy() {
        if (functional instanceof Consumer) {
            final Consumer<P> proxy = this::closureConsumer;
            return (T) proxy;
        } else if (functional instanceof UnaryOperator) {
            final UnaryOperator<P> proxy = this::closureUnaryOperator;
            return (T) proxy;
        } else if (functional instanceof Function) {
            final Function<P, R> proxy = this::closureFunction;
            return (T) proxy;
        } else if (functional instanceof Supplier) {
            final Supplier<R> proxy = this::closureSupplier;
            return (T) proxy;
        } else if (functional instanceof Runnable) {
            final Runnable proxy = this::closureRunnable;
            return (T) proxy;
        }
        throw new IllegalArgumentException("参数 functional" + functional + " 必须是支持的函数式接口");
    }

    /**
     * 返回自定义的闭包代理函数式接口实例
     *
     * @param function 自定义闭包代理函数式接口实例返回函数
     * @return 自定义的闭包代理函数式接口实例
     * @param <V> 自定义的返回函数式接口类型
     */
    public <V> V proxy(Function<Function<P, R>, V> function) {
        return function.apply(this::closureFunction);
    }

    /**
     * 根据函数式接口实例, 生成默认的针对(函数接口then调用的处理方式)的处理函数
     *
     * @return 函数接口then调用的处理方式 的处理函数
     */
    @SuppressWarnings("unchecked")
    private BiFunction<T, P, R> geneDefaultBiFunction() {
        if (functional instanceof Consumer) {
            return (t, p) -> {
                Consumer<P> consumer = (Consumer<P>) t;
                consumer.accept(p);
                return null;
            };
        } else if (functional instanceof UnaryOperator) {
            return (t, p) -> {
                UnaryOperator<P> consumer = (UnaryOperator<P>) t;
                return (R) consumer.apply(p);
            };
        } else if (functional instanceof Function) {
            return (t, p) -> {
                Function<P, R> consumer = (Function<P, R>) t;
                return consumer.apply(p);
            };
        } else if (functional instanceof Supplier) {
            return (t, p) -> {
                Supplier<R> supplier = ((Supplier<R>) t);
                return supplier.get();
            };
        } else if (functional instanceof Runnable) {
            return (t, p) -> {
                ((Runnable) t).run();
                return null;
            };
        }

        throw new IllegalArgumentException("参数 functional" + functional + " 必须是支持的函数式接口");
    }

}
