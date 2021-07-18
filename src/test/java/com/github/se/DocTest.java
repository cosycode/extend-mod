package com.github.se;

import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.ext.api.SerialRunnable;
import com.github.cosycode.ext.hub.OnceExecClosureProxy;
import com.github.cosycode.ext.se.util.LambdaUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/8
 *
 * @author CPF
 * @since
 **/
@Slf4j
public class DocTest {

    @Test
    public void testBtn3() {

        Test89 test89 = (int st, Object dfd, String... integers) -> {
            Throws.con(10, Thread::sleep);
            Map<String, Object> map = new HashMap<>();
            for (String key : integers) {
                map.put(key, st);
            }
            map.put("haads", dfd);
            return map;
        };

        // 将 Test89 转换为 BiFunction<>
        BiFunction<Test89, Object[], Map<String, Object>> biFunction = (t, o) -> t.fjk((int) o[0], (Object) o[1], (String[]) o[2]);

        //
        // proxy 将 Function<P, R> 转换为 testBtn3
        final Test89 proxy = new OnceExecClosureProxy<>(test89, biFunction).proxy(f -> (int st, Object dfd, String... integers) ->
                f.apply(new Object[]{st, dfd, integers}));

        IntStream.range(1, 20).parallel().forEach(it -> {
            final Map<String, Object> fjk = proxy.fjk(it, new Object(), "5, 54", "gjhksd", "jhhjdf", "jkk");
            System.out.println(fjk);
        });

    }

    @Test
    public void testBtn5() {

        Test89 test89 = (int st, Object dfd, String... integers) -> {
            Throws.con(10, Thread::sleep);
            Map<String, Object> map = new HashMap<>();
            for (String key : integers) {
                map.put(key, st);
            }
            map.put("haads", dfd);
            return map;
        };

        // 将 Test89 转换为 BiFunction<>
        BiFunction<Test89, Object[], Map<String, Object>> biFunction = (t, o) -> t.fjk((int) o[0], (Object) o[1], (String[]) o[2]);

        //
        // proxy 将 Function<P, R> 转换为 testBtn3
        final Test89 proxy = new OnceExecClosureProxy<>(test89, biFunction).proxy(f -> (int st, Object dfd, String... integers) ->
                f.apply(new Object[]{st, dfd, integers}));

        IntStream.range(1, 20).parallel().forEach(it -> {
            final Map<String, Object> fjk = proxy.fjk(it, new Object(), "5, 54", "gjhksd", "jhhjdf", "jkk");
            System.out.println(fjk);
        });

    }

    @Test
    public void testBtn4() {
        DocTest test = new DocTest();
        final SerialRunnable testBtn3 = test::testBtn3;
        final SerializedLambda serializedLambda = LambdaUtils.getSerializedLambda(testBtn3);
        System.out.println(serializedLambda.getImplMethodName());
        System.out.println(767);
    }

    interface Test89 {
        Map<String, Object> fjk(int st, Object dfd, String... integers);
    }

}
