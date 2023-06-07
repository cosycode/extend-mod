package com.github.cosycode.ext.dataformat;


import java.lang.reflect.Field;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/10/17
 * </p>
 *
 * @author pengfchen
 * @since 0.2.2
 **/
public class BeanUtils {

    private BeanUtils() {}

    public static String[] getFieldString(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        String[] filedString = new String[declaredFields.length];
        for (int i = 0, len = declaredFields.length; i < len; i++) {
            filedString[i] = declaredFields[i].getName();
        }
        return filedString;
    }

    public static <T> Object[] getValueObj(Class<? super T> clazz, T t) {
        Field[] declaredFields = clazz.getDeclaredFields();
        Object[] fieldObj = new Object[declaredFields.length];
        for (int i = 0, len = declaredFields.length; i < len; i++) {
            Field field = declaredFields[i];
            field.setAccessible(true);
            try {
                fieldObj[i] = field.get(t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return fieldObj;
    }

    public static <T> String[] getStringValueObj(Class<? super T> clazz, T t) {
        Field[] declaredFields = clazz.getDeclaredFields();
        String[] stringArr = new String[declaredFields.length];
        for (int i = 0, len = declaredFields.length; i < len; i++) {
            Field field = declaredFields[i];
            field.setAccessible(true);
            try {
                Object obj = field.get(t);
                if (obj == null) {
                    stringArr[i] = "";
                } else {
                    stringArr[i] = TypeConverter.convertObjToString(obj);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return stringArr;
    }

}
