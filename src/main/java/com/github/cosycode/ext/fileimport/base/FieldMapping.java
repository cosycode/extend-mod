package com.github.cosycode.ext.fileimport.base;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * <b>Description : </b> 用于一个字段映射对象
 * <p>
 * <b>created in </b> 2019/8/15
 * </p>
 *
 * @author CPF
 **/
public class FieldMapping<T> {

    /**
     * 字段名称
     */
    @Getter
    private final String fieldName;

    /**
     * 字段类型
     */
    @Getter
    private final Class<T> type;

    /**
     * 字段是否必要
     */
    @Getter
    private final boolean require;

    /**
     * 字段验证正则表达式
     */
    @Getter
    private final String ruleRegex;

    /**
     * 默认对象
     */
    @Getter
    private final Object defaultObj;
    /**
     * 当字段是一个字典表时, 验证时用 codeItemArray 数组进行验证, 此时 ruleRegex 无效.
     */
    private final String[] codeArr;
    private final String[] textArr;
    private final String separator;
    private final Function<Object, ?> function;
    /**
     * 仅用作缓存对象, 防止多次编译
     */
    private Pattern pattern;

    public FieldMapping(@NonNull String fieldName, @NonNull Class<T> type, boolean require, String ruleRegex, Object defaultObj) {
        this(fieldName, type, require, ruleRegex, defaultObj, null, null, null, null);
    }

    public FieldMapping(@NonNull String fieldName, @NonNull Class<T> type, boolean require, String[] codeArr, String[] textArr) {
        this(fieldName, type, require, null, null, codeArr, textArr, null, null);
    }

    public FieldMapping(@NonNull String fieldName, @NonNull Class<T> type, boolean require, String[] codeArr, String[] textArr, String multipleSeparator) {
        this(fieldName, type, require, null, null, codeArr, textArr, multipleSeparator, null);
    }

    public FieldMapping(@NonNull String fieldName, @NonNull Class<T> type, boolean require, Function<Object, ?> function) {
        this(fieldName, type, require, null, null, null, null, null, function);
    }

    private FieldMapping(String fieldName, Class<T> type, boolean require, String ruleRegex, Object defaultObj, String[] codeArr, String[] textArr, String separator, Function<Object, ?> function) {
        this.fieldName = fieldName;
        this.type = type;
        this.require = require;
        this.ruleRegex = ruleRegex;
        this.defaultObj = defaultObj;
        this.codeArr = codeArr;
        this.textArr = textArr;
        this.separator = separator;
        this.function = function;
    }

    private boolean checkRegex(String value) {
        if (ruleRegex == null) {
            return true;
        }
        if (pattern == null) {
            pattern = Pattern.compile(ruleRegex);
        }
        return pattern.matcher(value).matches();
    }


    /**
     * 对解析后的cell数据进行后续的检查, 解析操作
     * 1. 如果有自定义编程函数, 则执行返回
     * 2. 如果当前数据为字典表, 那么 value 如果是字典表的 code 值, 则返回 code 值, 如果是字典表的 text 值, 则也返回 text 值;
     * 3. 如果不是字典表, 则检查 是否有正则表达式判定, 如果有, 则判定, 若没有则直接返回 value;
     */
    public Object resolveValue(Object value) throws ParseException {
        // 如果有自定义编程函数, 则执行返回
        if (function != null) {
            return function.apply(value);
        }
        // 当前字段存到数据库里面的值为代码项
        if (value instanceof String) {
            String strVal = ((String) value).trim();
            // 当前字段存到数据库里面的值为代码项
            if (codeArr != null) {
                // 若 separator 不为空, 则说明字段中的值是以 separator 分割的复选值
                if (StringUtils.isNotBlank(separator)) {
                    String[] strings = strVal.split(separator);
                    List<String> valueList = new ArrayList<>(strings.length);
                    for (String it : strings) {
                        it = it.trim();
                        if (!"".equals(it)) {
                            valueList.add(getDbSaveValue(it));
                        }
                    }
                    return StringUtils.join(valueList, separator);
                }
                return getDbSaveValue(strVal);
            }
            if (pattern != null && !checkRegex(strVal)) {
                throw new ParseException("数据规则验证失败", 0);
            }
            return strVal;
        }
        return value;
    }


    /**
     * 有映射时优先识别为映射.
     */
    private String getDbSaveValue(String value) throws ParseException {
        if (textArr != null) {
            for (int i = 0, len = textArr.length; i < len; i++) {
                if (textArr[i].endsWith(value)) {
                    return codeArr[i];
                }
            }
        }
        // 如果没有找到映射text, 找到符合的code也行
        if (Arrays.asList(codeArr).contains(value)) {
            return value;
        }
        throw new ParseException("未找到相匹配的数据字典项", 0);
    }

}
