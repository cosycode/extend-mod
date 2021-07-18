package com.github.cosycode.ext.api;

import java.util.function.Function;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/9
 *
 * @author CPF
 **/
@FunctionalInterface
public interface SerialFunction<T, R> extends SerialFunctional, Function<T, R> {
}
