package com.github.cosycode.ext.hub;

import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/7
 *
 * @author CPF
 * @since
 **/
public class CurrentLimitClosureProxy<T, P, R> extends AbstractClosureProxy<T, P, R>{


    protected CurrentLimitClosureProxy(@NonNull T then) {
        super(then);
    }

    protected CurrentLimitClosureProxy(@NonNull T then, @NonNull BiFunction<T, P, R> function) {
        super(then, function);
    }

    protected CurrentLimitClosureProxy(@NonNull T then, @NonNull BiConsumer<T, P> biConsumer) {
        super(then, biConsumer);
    }

    @Override
    public R closureFunction(P params) {
        return null;
    }
}
