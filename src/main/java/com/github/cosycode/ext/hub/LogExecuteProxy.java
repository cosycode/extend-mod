package com.github.cosycode.ext.hub;

import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
 **/
public class LogExecuteProxy<T, P, R> extends AbstractClosureProxy<T, P, R> {

    public LogExecuteProxy(T then) {
        super(then);
    }

    public LogExecuteProxy(T then, BiFunction<T, P, R> function) {
        super(then, function);
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
        return biFunction.apply(functional, params);
    }

}
