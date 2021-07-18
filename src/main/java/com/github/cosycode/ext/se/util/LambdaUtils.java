package com.github.cosycode.ext.se.util;

import com.github.cosycode.common.ext.hub.Throws;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/7
 *
 * @author CPF
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LambdaUtils {

    private static Map<Class<?>, SerializedLambda> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();

    /**
     * 当对象中没有
     *
     * @param functional 函数接口对象
     * @return 函数接口对象所属的对象
     */
    public static Object getInstanceByFunctional(@NonNull Object functional) {
        final Field[] declaredFields = functional.getClass().getDeclaredFields();
        if (declaredFields.length < 1) {
            return null;
        }
        final Field declaredField = declaredFields[0];
        declaredField.setAccessible(true);
        return Throws.fun(functional, declaredField::get).runtimeExp().value();
    }

    public static Object getLambdaType(@NonNull Object object) {
        final Method[] declaredMethods = object.getClass().getDeclaredMethods();
        final Method method;
        if (declaredMethods.length == 0) {
            return null;
        } else if (declaredMethods.length == 1) {
            method = declaredMethods[0];
        } else {
            throw new IllegalArgumentException("TODO 需要处理多个");
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final int length = parameterTypes.length;
        final Class<?> returnType = method.getReturnType();
        if (void.class.equals(returnType)) {
            if (length == 0) {
                return Runnable.class;
            } else if (length == 1) {
                return Consumer.class;
            } else if (length == 2) {
                return BiConsumer.class;
            } else {
                throw new RuntimeException("fjkd");
            }
        } else {
            if (length == 0) {
                return Supplier.class;
            } else if (length == 1) {
                return Function.class;
            } else if (length == 2) {
                return BiFunction.class;
            } else {
                throw new RuntimeException("fjkfdfddd");
            }
        }
    }

    /**
     * 关键在于这个方法
     */
    public static SerializedLambda getSerializedLambda(Serializable fn) {
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass());
        if (lambda == null) {
            try {
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_LAMBDA_CACHE.put(fn.getClass(), lambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lambda;
    }


}
