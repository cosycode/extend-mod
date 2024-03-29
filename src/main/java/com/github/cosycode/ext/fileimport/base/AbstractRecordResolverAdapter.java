package com.github.cosycode.ext.fileimport.base;

import com.github.cosycode.common.base.IClassType;
import com.github.cosycode.ext.fileimport.excel.ParsedSheetHandler;
import com.github.cosycode.ext.fileimport.excel.SheetInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * <b>Description : </b> 解析一个sheet的适配器
 * <p>
 * <b>created in </b> 2019/8/16
 * </p>
 *
 * @author CPF
 **/
public abstract class AbstractRecordResolverAdapter<T> implements IClassType<T> {

    /**
     * excel 行的映射对象
     */
    private RecordMapping sheetBeanMapping;

    /**
     * 获取sheet和Bean的映射SheetBeanMapping对象
     */
    public final RecordMapping getSheetBeanMapping() {
        if (sheetBeanMapping == null) {
            sheetBeanMapping = new RecordMapping();
            completeSheetBeanMapping(sheetBeanMapping);
        }
        return sheetBeanMapping;
    }

    /**
     * 完善 SheetMapping
     */
    protected abstract void completeSheetBeanMapping(RecordMapping mapping);

    /**
     * 该方法用于判断适配器是否匹配待处理的Sheet对象
     *
     * @param recordFile sheet名称
     */
    public abstract boolean isMatch(Object recordFile);

    /**
     * 对解析后的sheet进行二次处理
     *
     * @param maps 解析后的sheet数据
     * @return list
     */
    protected abstract List<ParsedSheetHandler<?>> disposeParsedSheet(SheetInfo sheetInfo, List<Map<String, Object>> maps) throws InstantiationException,
            IllegalAccessException,
            InvocationTargetException;

}
