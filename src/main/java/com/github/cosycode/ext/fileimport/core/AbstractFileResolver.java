package com.github.cosycode.ext.fileimport.core;

import lombok.NonNull;

import java.io.InputStream;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/7/22 17:21
 */
public abstract class AbstractFileResolver {

    /**
     * 使用规定的解析器解析excel文件, 并将解析的结果返回
     *
     * @param is       文件流
     * @param fileName 文件名称
     * @param adapter  文件解析适配器
     * @return Excel 解析后的 Map<AbstractSheetBeanMappingAdapter, List<Bean>>
     */
    public abstract AbstractParsedDataHandler resolve(@NonNull InputStream is, String fileName, AbstractFileImportAdapter adapter);

}
