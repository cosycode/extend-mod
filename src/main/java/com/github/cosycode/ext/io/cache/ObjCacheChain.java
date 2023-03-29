package com.github.cosycode.ext.io.cache;

import com.github.cosycode.common.lang.BaseRuntimeException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * <b>Description : </b> 改对象维持一个 ICacheStack 的对象列表,
 * 获取元素的时候从头开始, 从 ICacheStack 的实例对象里面获取数据.
 * 获取到则直接返回, 获取不到则到下一个 ICacheStack 的实例对象里面获取元素, 直到获取到元素为止.
 * 获取到之后, 还会讲元素存储到前面的 ICacheStack 实例中去.
 * <p>
 * <b>created in </b> 2022/12/8
 * </p>
 *
 * @author pengfchen
 * @since 0.2.2
 **/
@Slf4j
@AllArgsConstructor
public class ObjCacheChain<T extends ICacheStack> {

    final List<AbstractObjCacheHandler<T>> cacheHandlerList;

    public T getData() {
        Objects.requireNonNull(cacheHandlerList);
        if (cacheHandlerList.isEmpty()) {
            throw new BaseRuntimeException("cacheHandlerList is empty");
        }
        AbstractObjCacheHandler<T> firstHandler = cacheHandlerList.get(0);
        T t = firstHandler.get();
        if (t == null || !firstHandler.validate(t)) {
            synchronized (cacheHandlerList) {
                t = firstHandler.get();
                if (t == null || ! firstHandler.validate(t)) {
                    Iterator<AbstractObjCacheHandler<T>> iterator = cacheHandlerList.iterator();
                    return getData(iterator);
                }
            }
        }
        return t;
    }

    /**
     * 该方法不应该返回 null, 在调用该方法之前, 请确保 iterator 中的最后一个 AbstractCacheHandler<T> 对象实例一定可以获取到合法的对象实例.
     *
     * @param iterator 迭代器
     * @return 从 iterator 里面获取到的对象.
     */
    private T getData(@NonNull Iterator<AbstractObjCacheHandler<T>> iterator) {
        if (! iterator.hasNext()) {
            // 假如说最后 一级从 http 上面获取, 那么若该级别的 get 获取不到合法数据, 那么还应该有下一级.
            throw new BaseRuntimeException("cacheHandlerList 里面已经空了, 请确保最后一个 AbstractCacheHandler<T> 实例对象可以正确获取到元素");
        }
        /* 若是获取到元素则返回, */
        AbstractObjCacheHandler<T> item = iterator.next();
        T pop = item.get();
        if (pop != null) {
            boolean validate = item.validate(pop);
            if (validate) {
                return pop;
            } else {
                log.debug("{}[{}] 中获取到的元素验证失败, 清除失效元素", item.getClass(), item.getTag());
                item.clear();
            }
        }
        pop = getData(iterator);
        // 从下一个 AbstractCacheHandler<T> 获取到的元素不应该为 null
        Objects.requireNonNull(pop);
        log.debug("获取到有效元素, 将有效元素缓存进 {}[{}]", item.getClass(), item.getTag());
        item.put(pop);
        return pop;
    }

}
