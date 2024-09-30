package com.github.cosycode.ext.fileimport.core;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2020/7/29
 * </p>
 *
 * @author CPF
 **/
public abstract class AbstractPersistenceResult {

    Map<String, Object> map;

    @Override
    public String toString() {
        return map.entrySet().stream().map(entry -> String.format("<br> =>sheet : %s --> imported %s records", entry.getKey(), entry.getValue())).collect(Collectors.joining(""));
    }
}
