package com.github.se;

import lombok.Getter;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
 * @since
 **/
public class AnoTest {

    public static class IType<T> {

        @Getter
        private final T type;

        public IType(T t) {
            this.type = t;
        }

    }


}
