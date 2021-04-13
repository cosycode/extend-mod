package com.github.se;

import com.github.cosycode.ext.hub.SingletonClosureProxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class SingletonClosureProxyTest {

    public static String geneDemo() {
        return UUID.randomUUID().toString();
    }

    @Test
    public void test1() {
        System.out.println("代理前");
        Supplier<Object> newObject = SingletonClosureProxyTest::geneDemo;
        System.out.println(newObject.get());
        System.out.println(newObject.get());
        System.out.println(newObject.get());
        System.out.println(newObject.get());
        System.out.println(newObject.get());

        newObject = SingletonClosureProxy.of(newObject);
        System.out.println("代理后");
        System.out.println(newObject.get());
        System.out.println(newObject.get());
        System.out.println(newObject.get());
        System.out.println(newObject.get());
        System.out.println(newObject.get());

    }

}
