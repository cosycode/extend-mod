package com.github.cosycode.ext.dataformat;

import com.github.cosycode.common.lang.BaseRuntimeException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import lombok.NonNull;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/9/6
 * </p>
 *
 * @author pengfchen
 * @since 0.2.2
 **/
public class CsvUtils {

    private CsvUtils() {
    }

    /**
     * 读取csv，返回一个Iterator<String[]>，每个String[]表示csv中的一行数据
     *
     * @param csvFile csv 文件 对象
     * @return List<String [ ]> 读取的数据
     * @throws IOException
     * @throws CsvException
     */
    public static List<String[]> readCSV(File csvFile) throws IOException, CsvException {
        DataInputStream dataInputStream = new DataInputStream(Files.newInputStream(csvFile.toPath()));
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(dataInputStream, StandardCharsets.UTF_8))) {
            return csvReader.readAll();
        }
    }


    /**
     * @param csvFile csv 文件
     * @param tClass  模版类
     * @param <T>     模版类
     * @return 读取的 list
     * @throws IOException
     * @throws CsvException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> readCsvToBeanList(File csvFile, Class<T> tClass) throws IOException, CsvException, InstantiationException, IllegalAccessException, ParseException {
        List<String[]> strings = readCSV(csvFile);
        if (strings == null) {
            return new ArrayList<>();
        }

        // 封装函数
        Field[] fields = tClass.getDeclaredFields();
        Function<String, Field> function = name -> {
            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase(name)) {
                    return field;
                }
            }
            return null;
        };
        // 获取header
        List<Field> list = new ArrayList<>();
        String[] headerString = strings.get(0);
        for (String str : headerString) {
            str = str.trim();
            Field apply = function.apply(str);
            if (apply != null) {
                apply.setAccessible(true);
                list.add(apply);
            }
        }
        Field[] header = list.toArray(new Field[0]);
        // 获取 body
        List<T> result = new ArrayList<>();
        int len = header.length;
        int rowNum = 1;
        for (int rowLen = strings.size(); rowNum < rowLen; rowNum++) {
            String[] data = strings.get(rowNum);
            T t = tClass.newInstance();
            for (int i = 0; i < len; i++) {
                Field field = header[i];
                if (field == null) {
                    continue;
                }
                String da = data[i] == null ? "" : data[i].trim();
                Class<?> type = field.getType();
                try {
                    if (!da.isEmpty()) {
                        Object obj = TypeConverter.convertStringToObj(da, type);
                        field.set(t, obj);
                    }
                } catch (IllegalArgumentException e) {
                    throw new BaseRuntimeException("convertStringToObj error, data is " + Arrays.toString(data), e);
                }
            }
            result.add(t);
        }
        return result;
    }


    public static <T> void writeBeanListToCsvFile(File file, @NonNull List<T> list, Class<? super T> tClass) throws IOException {
        if (list.isEmpty()) {
            return;
        }
        Writer writer = new FileWriter(file);
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] fieldString = BeanUtils.getFieldString(tClass);
            csvWriter.writeNext(fieldString);
            for (T obj : list) {
                String[] strings = BeanUtils.getStringValueObj(tClass, obj);
                csvWriter.writeNext(strings);
            }
        }
    }


    /**
     * <p>
     * 将 csv 中的行首 和 一行数据转换成一个 Map
     * </p>
     *
     * @param keys   json的key，顺序需与csv中的value对应
     * @param values csv中数据作为value
     */
    public static Map<String, Object> convertMap(String[] keys, String[] values) {
        Map<String, Object> json = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            json.put(keys[i], values[i]);
        }
        return json;
    }

    /**
     * <p>
     * 将 csv 中的行首 和 多行数据转换成一个 MapList
     * </p>
     *
     * @param keys        行首，顺序需与csv中的value对应
     * @param stringsList 读取csv返回的List<String[]>
     */
    public static List<Map<String, Object>> convertMap(String[] keys, List<String[]> stringsList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (String[] strings : stringsList) {
            Map<String, Object> json = convertMap(keys, strings);
            mapList.add(json);
        }
        return mapList;
    }

}
