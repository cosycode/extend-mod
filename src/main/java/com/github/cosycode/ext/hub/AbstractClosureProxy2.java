package com.github.cosycode.ext.hub;

import lombok.NonNull;

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
public abstract class AbstractClosureProxy2<T, P, P1, R> {

    /**
     * 代理方法的函数式接口实例
     */
    protected final T funExpress;

    /**
     * 函数式接口 then 对传入参数的操作函数, 如果该项为 null, 则默认
     */
    protected final BiFunction<T, P, R> biFunction;

    protected AbstractClosureProxy2(@NonNull T funExpress) {
        this.funExpress = funExpress;
        biFunction = geneDefaultBiFunction(funExpress);
    }

    protected AbstractClosureProxy2(@NonNull T funExpress, @NonNull BiFunction<T, P, R> function) {
        this.funExpress = funExpress;
        this.biFunction = function;
    }

    protected AbstractClosureProxy2(@NonNull T funExpress, @NonNull BiConsumer<T, P> biConsumer) {
        this.funExpress = funExpress;
        this.biFunction = (t, p) -> {
            biConsumer.accept(t, p);
            return null;
        };
    }

    public abstract R closureBiFunction(P p0, P1 p1);

    public void closureBiConsumer(P p0, P1 p1) {
        closureBiFunction(p0, p1);
    }

    /**
     * 闭包代理方法: Function
     */
    public R closureFunction(P params) {
        return closureBiFunction(params, null);
    }

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

//    public boolean closureBiPredicate(P p0, P1 p1) {
//        return (boolean) closureBiFunction(p0, p1);
//    }

//    代表了一个作用于于两个同类型操作符的操作，并且返回了操作符同类型的结果
//    public R closureBinaryOperator(R p0, R p1) {
//        return closureBiFunction((R)p0, (R)p1);
//    }

//
//5	BooleanSupplier
//            代表了boolean值结果的提供方
//
//6	Consumer<T>
//            代表了接受一个输入参数并且无返回的操作
//
//7	DoubleBinaryOperator
//    代表了作用于两个double值操作符的操作，并且返回了一个double值的结果。
//
//            8	DoubleConsumer
//    代表一个接受double值参数的操作，并且不返回结果。
//
//            9	DoubleFunction<R>
//    代表接受一个double值参数的方法，并且返回结果
//
//10	DoublePredicate
//            代表一个拥有double值参数的boolean值方法
//
//11	DoubleSupplier
//            代表一个double值结构的提供方
//
//12	DoubleToIntFunction
//    接受一个double类型输入，返回一个int类型结果。
//
//            13	DoubleToLongFunction
//    接受一个double类型输入，返回一个long类型结果
//
//14	DoubleUnaryOperator
//    接受一个参数同为类型double,返回值类型也为double 。
//
//            15	Function<T,R>
//    接受一个输入参数，返回一个结果。
//
//            16	IntBinaryOperator
//    接受两个参数同为类型int,返回值类型也为int 。
//
//            17	IntConsumer
//    接受一个int类型的输入参数，无返回值 。
//
//            18	IntFunction<R>
//    接受一个int类型输入参数，返回一个结果 。
//
//            19	IntPredicate
//：接受一个int输入参数，返回一个布尔值的结果。
//
//            20	IntSupplier
//    无参数，返回一个int类型结果。
//
//            21	IntToDoubleFunction
//    接受一个int类型输入，返回一个double类型结果 。
//
//            22	IntToLongFunction
//    接受一个int类型输入，返回一个long类型结果。
//
//            23	IntUnaryOperator
//    接受一个参数同为类型int,返回值类型也为int 。
//
//            24	LongBinaryOperator
//    接受两个参数同为类型long,返回值类型也为long。
//
//            25	LongConsumer
//    接受一个long类型的输入参数，无返回值。
//
//            26	LongFunction<R>
//    接受一个long类型输入参数，返回一个结果。
//
//            27	LongPredicate
//    R接受一个long输入参数，返回一个布尔值类型结果。
//
//            28	LongSupplier
//    无参数，返回一个结果long类型的值。
//
//            29	LongToDoubleFunction
//    接受一个long类型输入，返回一个double类型结果。
//
//            30	LongToIntFunction
//    接受一个long类型输入，返回一个int类型结果。
//
//            31	LongUnaryOperator
//    接受一个参数同为类型long,返回值类型也为long。
//
//            32	ObjDoubleConsumer<T>
//    接受一个object类型和一个double类型的输入参数，无返回值。
//
//            33	ObjIntConsumer<T>
//    接受一个object类型和一个int类型的输入参数，无返回值。
//
//            34	ObjLongConsumer<T>
//    接受一个object类型和一个long类型的输入参数，无返回值。
//
//            35	Predicate<T>
//    接受一个输入参数，返回一个布尔值结果。
//
//            36	Supplier<T>
//    无参数，返回一个结果。
//
//            37	ToDoubleBiFunction<T,U>
//    接受两个输入参数，返回一个double类型结果
//
//38	ToDoubleFunction<T>
//    接受一个输入参数，返回一个double类型结果
//
//39	ToIntBiFunction<T,U>
//    接受两个输入参数，返回一个int类型结果。
//
//            40	ToIntFunction<T>
//    接受一个输入参数，返回一个int类型结果。
//
//            41	ToLongBiFunction<T,U>
//    接受两个输入参数，返回一个long类型结果。
//
//            42	ToLongFunction<T>
//    接受一个输入参数，返回一个long类型结果。
//
//            43	UnaryOperator<T>
//    接受一个参数为类型T,返回值类型也为T。
    /**
     * 根据 then 返回默认的闭包代理方法
     *
     * @return 默认的闭包代理方法
     */
    @SuppressWarnings("unchecked")
    public T proxy() {
        if (funExpress instanceof Consumer) {
            final Consumer<P> proxy = this::closureConsumer;
            return (T) proxy;
        } else if (funExpress instanceof Function) {
            final Function<P, R> proxy = this::closureFunction;
            return (T) proxy;
        } else if (funExpress instanceof Supplier) {
            final Supplier<R> proxy = this::closureSupplier;
            return (T) proxy;
        } else if (funExpress instanceof Runnable) {
            final Runnable proxy = this::closureRunnable;
            return (T) proxy;
        }
        throw new IllegalArgumentException("参数 funExpress 必须是支持的函数式接口");
    }

    /**
     * 根据函数式接口实例, 生成默认的针对(函数接口then调用的处理方式)的处理函数
     *
     * @param funExpress 函数式接口实例
     * @return 函数接口then调用的处理方式 的处理函数
     */
    @SuppressWarnings("unchecked")
    private BiFunction<T, P, R> geneDefaultBiFunction(T funExpress) {
        if (funExpress instanceof Consumer) {
            return (t, p) -> {
                Consumer<P> consumer = (Consumer<P>) t;
                consumer.accept(p);
                return null;
            };
        } else if (funExpress instanceof Function) {
            return (t, p) -> {
                Function<P, R> consumer = (Function<P, R>) t;
                consumer.apply(p);
                return null;
            };
        } else if (funExpress instanceof Supplier) {
            return (t, p) -> {
                Supplier<R> supplier = ((Supplier<R>) t);
                return supplier.get();
            };
        } else if (funExpress instanceof Runnable) {
            return (t, p) -> {
                ((Runnable) t).run();
                return null;
            };
        }
        throw new IllegalArgumentException("参数 funExpress 必须是支持的函数式接口");
    }

}
