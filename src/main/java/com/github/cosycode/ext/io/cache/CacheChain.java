package com.github.cosycode.ext.io.cache;

import com.github.cosycode.common.override.java.util.LinkedListCopy;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/12/8
 * </p>
 *
 * @author pengfchen
 * @since 0.2.2
 **/
@AllArgsConstructor
public class CacheChain <T extends ICacheStack> {

    @Setter
    LinkedListCopy<AbstractCacheHandler<T>> chain;

    public T getData() {
        LinkedListCopy.Node<AbstractCacheHandler<T>> firstNode = chain.getFirstNode();
        Objects.requireNonNull(firstNode);
        return getData(chain.getFirstNode());
    }

    private T getData(LinkedListCopy.Node<AbstractCacheHandler<T>> node) {
        AbstractCacheHandler<T> item = node.getItem();
        T pop = item.get();
        if (pop != null) {
            boolean validate = item.validate(pop);
            if (validate) {
                return pop;
            } else {
                item.clear();
            }
        }
        LinkedListCopy.Node<AbstractCacheHandler<T>> next = node.getNext();
        // 假如说最后 一级从 http 上面获取, 那么若该级别的 get 获取不到合法数据, 那么还应该有下一级.
        Objects.requireNonNull(next);
        // 不应该为空
        pop = getData(next);
        Objects.requireNonNull(pop);
        item.put(pop);
        return pop;
    }

}
