package com.github.cosycode.ext.api;

import java.util.function.Supplier;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/9
 *
 * @author CPF
 * @since
 **/
public interface SerialSupplier<T> extends SerialFunctional, Supplier<T> {
}
