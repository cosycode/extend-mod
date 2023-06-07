package com.github.cosycode.ext.dataformat;

import com.google.gson.Gson;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/10/17
 * </p>
 *
 * @author pengfchen
 * @since 0.2.2
 **/
public class TypeConverter {

    private TypeConverter(){}

    /**
     *
     * TODO type convert
     * @param obj
     * @return
     */
    public static String convertObjToString(Object obj) {
        if (obj == null) {
            return null;
        }
        Class<?> type = obj.getClass();
        if (type.equals(String.class)) {
            return (String) obj;
        } else if (type.equals(Boolean.class) || type.equals(Integer.class)) {
            return obj.toString();
        } else {
            return new Gson().toJson(obj);
        }
    }

    /**
     * 将一个字符串 转换成对应类型,
     * @return 转换成为的字符串类型
     */
    public static <T> T convertStringToObj(String string, Class<T> type) throws ParseException {
        if (type.equals(String.class)) {
            return (T) string;
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (T) (Boolean) Boolean.parseBoolean(string);
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            return (T) (Integer) Integer.parseInt(string);
        } else if (type.equals(Date.class)) {
            return (T) DateUtils.parseDate(string);
        } else if (type.equals(BigDecimal.class)) {
            return (T) new BigDecimal(string);
        } else {
            return new Gson().fromJson(string, type);
        }
    }

}
