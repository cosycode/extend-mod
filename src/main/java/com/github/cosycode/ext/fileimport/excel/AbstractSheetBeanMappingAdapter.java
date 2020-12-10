package com.github.cosycode.ext.fileimport.excel;

import com.github.cosycode.common.util.common.BeanUtils;
import com.github.cosycode.ext.fileimport.base.RecordMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * <b>Description : </b> 解析一个sheet的适配器
 *
 * @author CPF
 * @date 2019/8/16 11:11
 **/
public abstract class AbstractSheetBeanMappingAdapter<T> {

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
     * 该方法用于判断适配器是否匹配待处理的Sheet对象
     *
     * @param sheetName sheet名称
     */
    public abstract boolean isMatchSheetName(String sheetName);

    /**
     * 完善 SheetMapping
     */
    protected abstract void completeSheetBeanMapping(RecordMapping mapping);

    /**
     * 获取当前类 T 后代继承的 T.class
     */
    @SuppressWarnings("unchecked")
    public Class<T> getTemplateClass() {
        Class<?> clazz = getClass();
        while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }
        Type type = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        return (Class<T>) type;
    }

    /**
     * 对解析后的sheet进行二次处理
     *
     * @param maps 解析后的sheet数据
     * @return list
     */
    protected ParsedSheetHandler<T> disposeParsedSheet(SheetInfo sheetInfo, List<Map<String, Object>> maps) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        // 获取当前类 T 后代继承的 T.class
        Class<T> tClass = getTemplateClass();
        // 将数据转化为 list
        List<T> list = BeanUtils.mapListToBeanList(maps, tClass);
        // 转换之后最后执行操作
        list.forEach(this::completeBean);
        // 加入导入模板
        return new ParsedSheetHandler<>(sheetInfo, list, this::batchImport);
    }

    /**
     * 解析之后, 导入之前对Bean进行处理
     */
    protected abstract void completeBean(T bean);

    /**
     * 批量导入
     */
    protected abstract int batchImport(List<T> quoteList);
}
