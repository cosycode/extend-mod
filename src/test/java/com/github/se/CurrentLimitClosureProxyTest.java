package com.github.se;

import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.ext.hub.CurrentLimitClosureProxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/9
 *
 * @author CPF
 * @since 0.1.0
 **/
@Slf4j
public class CurrentLimitClosureProxyTest {

    /**
     * 当前方法在同一时间内只能够有一个线程执行业务逻辑
     */
    private void runDemo(String msg) {
        log.info("start {}", msg);
        {
            // ---- 业务逻辑(用睡眠1s 表示) ----
            Throws.con(1000, Thread::sleep);
        }
        log.info(" end  {}", msg);
    }

    /**
     * 普通情况下 开启 8 个线程执行
     */
    @Test
    public void test0() {
        CurrentLimitClosureProxyTest click = new CurrentLimitClosureProxyTest();
        IntStream.range(0, 8).parallel().mapToObj(Integer::toString).forEach(click::runDemo);
    }

    /**
     * 经过 CurrentLimitClosureProxy 处理后 开启8个线程执行
     */
    @Test
    public void test1() {
        CurrentLimitClosureProxyTest click = new CurrentLimitClosureProxyTest();
        // 通过 CurrentLimitClosureProxy 类代理, 同一时间只能够由两个线程执行方法
        final Consumer<String> proxy = new CurrentLimitClosureProxy<>(2, (Consumer<String>) click::runDemo).proxy();
        IntStream.range(0, 8).parallel().mapToObj(Integer::toString).forEach(proxy);
    }

}
