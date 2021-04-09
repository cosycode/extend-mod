package com.github.cosycode.ext.api;

import java.util.function.Function;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/9
 *
 * @author CPF
 * @since
 **/
@FunctionalInterface
public interface IFunction<T, R> extends Functional, Function<T, R> {
}
