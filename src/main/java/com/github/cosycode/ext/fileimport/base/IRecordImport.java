package com.github.cosycode.ext.fileimport.base;

import java.util.List;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2020/7/30
 * </p>
 *
 * @author CPF
 **/
@FunctionalInterface
public interface IRecordImport<T> {

    /**
     * 批量导入
     */
    int batchImport(List<T> quoteList);
}
