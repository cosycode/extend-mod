package com.github.se;

import lombok.Getter;
import org.junit.Ignore;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
 **/
@Ignore
public class AnoTest {

    public static class IType<T> {

        @Getter
        private final T type;

        public IType(T t) {
            this.type = t;
        }

    }


}
