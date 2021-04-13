package com.github.se;

import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.ext.hub.OnceExecClosureProxy;
import com.github.cosycode.ext.hub.OnceExecutes;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
public class OnceExecClosureProxyTest {

    /**
     * 当前方法在同一时间内只能够有一个线程执行业务逻辑
     */
    private void test(String msg) {
        log.info("start {}", msg);
        {
            // ---- 业务逻辑(用睡眠1mm 表示) ----
            Throws.con(1, Thread::sleep);
        }
        log.info(" end  {}", msg);
    }

    /**
     * 普通情况下 开启20个线程执行
     */
    @Test
    public void test0() {
        OnceExecClosureProxyTest click = new OnceExecClosureProxyTest();
        // 正常情况下开启10个线程执行
        IntStream.range(1, 10).parallel().mapToObj(Integer::toString).forEach(click::test);
    }

    /**
     * 经过 OnceExecutes 处理后 开启10个线程执行
     */
    @Test
    public void test1() {
        OnceExecClosureProxyTest click = new OnceExecClosureProxyTest();
        IntStream.range(1, 10).parallel().mapToObj(Integer::toString).forEach(OnceExecutes.consumer(click::test));
    }


    @Test
    public void testBtn1() {
        JButton btnConvert = new JButton("按钮");
        final ActionListener actionListener = e -> {
            log.info("执行转换数据操作 start");
            // 执行转换数据操作
            Throws.con(100, Thread::sleep);
            log.info("执行转换数据操作 end");
        };
        btnConvert.addActionListener(actionListener);

        // 模拟连续点击按钮10次
        IntStream.range(0, 10).parallel().forEach(btnConvert::doClick);
    }

    @Test
    public void testBtn2() {
        final ActionListener actionListener = e -> {
            log.info("执行转换数据操作 start");
            // 执行转换数据操作
            Throws.con(100, Thread::sleep);
            log.info("执行转换数据操作 end");
        };
        final ActionListener proxy = new OnceExecClosureProxy<>(actionListener, ActionListener::actionPerformed).proxy(f -> f::apply);

        JButton btnConvert = new JButton("转换");
        btnConvert.addActionListener(proxy);
        // 开启20个线程并发执行
        IntStream.range(1, 20).parallel().forEach(btnConvert::doClick);
    }



    @Test
    public void test2() {
        OnceExecClosureProxyTest click = new OnceExecClosureProxyTest();
        final Consumer<String> consumer = click::test;
        Function<String, Object> function = OnceExecutes.exec(consumer, (t, p) -> {
            t.accept(p);
            return null;
        });

        IntStream.range(1, 20).parallel().mapToObj(Integer::toString).forEach(function::apply);

        OnceExecClosureProxy.of(consumer);
    }

    @Test
    public void test3() {
        OnceExecClosureProxyTest click = new OnceExecClosureProxyTest();
        final Consumer<String> proxy = new OnceExecClosureProxy<>((Consumer<String>)click::test).proxy();
        IntStream.range(1, 200).parallel().mapToObj(Integer::toString).forEach(proxy);
    }


}
