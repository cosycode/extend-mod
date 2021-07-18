package com.github.se;

import com.github.cosycode.ext.hub.LogExecuteProxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/7
 *
 * @author CPF
 * @since 0.1.0
 **/
@Slf4j
public class FunctionTest {

    public void fun01() {
        log.info("call fun1");
    }

    public void fun02(int i) {
        log.info("call fun2");
    }

    public void fun03(String str) {
        log.info("call fun2");
    }

    public void fun04(Object[] objects) {
        log.info("call fun2");
    }

    public void fun05(String str, Map<String, Object> map) {
        log.info("call fun3");
    }

    public void fun06(String str, Integer... integers) {
        log.info("call fun2");
    }

    public String fun21() {
        log.info("call fun1");
        return UUID.randomUUID().toString();
    }

    public Integer fun22(int i) {
        log.info("call fun2");
        return i;
    }

    public String fun23(String str) {
        log.info("call fun2");
        return str + new Random().nextLong();
    }

    public Object fun24(Object[] objects) {
        log.info("call fun2");
        return objects == null ? objects : new Object();
    }

    public int fun25(String str, Map<String, Object> map) {
        log.info("call fun3");
        return new Random().nextInt(1000);
    }

    public Map<Integer, String> fun26(String str, Integer... integers) {
        log.info("call fun2");
        Map<Integer, String> map = new HashMap<>();
        if (integers != null) {
            Arrays.stream(integers).forEach(i -> map.put(i, str));
        }
        return map;
    }

    @Test
    public void test() {
        FunctionTest functionTest = new FunctionTest();

        final Runnable fun01Proxy = new LogExecuteProxy<>((Runnable) functionTest::fun01).proxy();
        fun01Proxy.run();

        final Consumer<Integer> fun02Proxy = new LogExecuteProxy<>((Consumer<Integer>) functionTest::fun02).proxy();
        fun02Proxy.accept(5);

        final Consumer<String> fun03Proxy = new LogExecuteProxy<>((Consumer<String>) functionTest::fun03).proxy();
        fun03Proxy.accept("哈哈3");

        final Function<String, String> fun13Proxy = new LogExecuteProxy<>((Function<String, String>) functionTest::fun23).proxy();
        final String apply = fun13Proxy.apply("哈哈3");
        System.out.println(apply);


        final UnaryOperator<String> fun13Proxy3 = new LogExecuteProxy<>((UnaryOperator<String>) functionTest::fun23).proxy();
        final String apply3 = fun13Proxy3.apply("哈哈3");
        System.out.println(apply3);


        final Function26 proxy = new LogExecuteProxy<>(functionTest::fun26,
                (Function26 f, Object[] p) -> f.fun26((String) p[0], (Integer) p[1])).proxy();
        proxy.fun26("haha", 9);


    }


    @FunctionalInterface
    interface Function26 {
        Map<Integer, String> fun26(String str, Integer... integers);
    }

}
