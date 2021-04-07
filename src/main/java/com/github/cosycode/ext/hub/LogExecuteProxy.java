package com.github.cosycode.ext.hub;

import lombok.Setter;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
 * @since 1.0
 **/
public class LogExecuteProxy<T, P, R> extends AbstractClosureProxy<T, P, R>{

    private final Lock lock = new ReentrantLock();

    @Setter
    private T skip;

    public LogExecuteProxy(T then) {
        super(then);
    }

    public LogExecuteProxy(T then, BiFunction<T, P, R> function) {
        super(then, function);
    }

    public LogExecuteProxy(T then, T skip, BiFunction<T, P, R> function) {
        super(then, function);
        this.skip = skip;
    }

    @Override
    public R closureFunction(P params) {
        if (params != null) {
            if (params.getClass().isArray()) {
                System.out.println(Arrays.toString((Object[]) params));
            } else {
                System.out.println(params);
            }
        } else {
            System.out.println("null");
        }
        return biFunction.apply(then, params);
    }

}
