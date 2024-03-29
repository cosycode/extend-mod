package com.github.cosycode.ext.se.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.cosycode.common.ext.bean.DoubleBean;
import com.github.cosycode.common.lang.BaseRuntimeException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/11/14
 * </p>
 *
 * @author pengfchen
 * @since 1.8
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    private static JsonParser jsonParser = new GsonParser();

    public interface Config {

        static void setJsonParser(@NonNull JsonParser jsonParser) {
            JsonUtils.jsonParser = jsonParser;
        }

    }

    private static JsonParser getJsonParser() {
        return jsonParser;
    }

    public static String toJson(Object obj) {
        return getJsonParser().toJson(obj);
    }

    public static String toFormatJson(Object obj) {
        return getJsonParser().toFormatJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getJsonParser().fromJson(json, classOfT);
    }

    public static <T> List<T> fromJsonArray(String json, Class<T> classOfT) {
        return getJsonParser().fromJsonArray(json, classOfT);
    }

    public static  <T> String processJson(String json, Class<T> tClass, UnaryOperator<T> operator) {
        return getJsonParser().processJson(json, tClass, operator);
    }

    /**
     * 确保转换时不丢失信息, 通过多次转换json对比转换的字符串.
     */
    public static <T> T fromJsonStrict(String json, Class<T> classOfT) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(classOfT);
        T t = fromJson(json, classOfT);
        String jsonAfter = toJson(t);
        if (json.equals(jsonAfter)) {
            return t;
        }
        String sortJson = toJson(fromJson(json, TreeMap.class));
        String sortJsonAfter = toJson(fromJson(json, TreeMap.class));
        if (sortJson.equals(sortJsonAfter)) {
            return t;
        }
        throw new BaseRuntimeException("json 转换成 classOf T, 信息丢失, \n原来的 json: %s, 排序后的json: %s, \n转换成 %s 后的json: %s",
            json, sortJson, classOfT.getName(), sortJsonAfter);
    }

    /**
     * 确保转换时不丢失信息, 通过多次转换json对比转换的字符串.
     */
    public static <T> List<T> fromJsonArrayStrict(String json, Class<T> classOfT) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(classOfT);
        List<T> tList = fromJsonArray(json, classOfT);
        String jsonAfter = toJson(tList);
        if (json.equals(jsonAfter)) {
            return tList;
        }
        String sortJson = toJson(fromJsonArray(json, TreeMap.class));
        String sortJsonAfter = toJson(fromJsonArray(json, TreeMap.class));
        if (sortJson.equals(sortJsonAfter)) {
            return tList;
        }
        throw new BaseRuntimeException("json 转换成 classOf T, 信息丢失, \n原来的 json: %s, 排序后的json: %s, \n转换成 List<%s> 后的json: %s",
            json, sortJson, classOfT.getName(), sortJsonAfter);
    }

    interface JsonParser {

        abstract String toJson(Object obj);

        abstract String toFormatJson(Object obj);

        abstract <T> T fromJson(String json, Class<T> classOfT);

        abstract <T> List<T> fromJsonArray(String json, Class<T> classOfT);

        /**
         * 将 json 转换为 类型 T, 由 operator 处理后, 再转换为 json 返回.
         *
         * @param json json字符串
         * @param tClass json 字符串对应的类
         * @param operator 函数式操作接口
         * @param <T> json 字符串对应的 类型
         * @return null(如果 operator 返回 null), 否则会返回 operator的返回值 转化后的 json
         */
        default  <T> String processJson(String json, Class<T> tClass, UnaryOperator<T> operator) {
            T before = fromJson(json, tClass);
            T after = operator.apply(before);
            if (after == null) {
                return null;
            }
            return toJson(after);
        }
    }

    public static class GsonParser implements JsonParser {

        @Override
        public String toJson(Object obj) {
            return new Gson().toJson(obj);
        }

        @Override
        public String toFormatJson(Object obj) {
            return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
        }

        @Override
        public <T> T fromJson(String json, Class<T> classOfT) {
            return new Gson().fromJson(json, classOfT);
        }

        @Override
        public <T> List<T> fromJsonArray(String json, Class<T> classOfT) {
            Class<T[]> arrClass = (Class<T[]>) Array.newInstance(classOfT, 0).getClass();
            return nativeFromJsonArray(json, arrClass);
        }

        private  <T> List<T> nativeFromJsonArray(String json, Class<T[]> classOfT) {
            T[] ts = new Gson().fromJson(json, classOfT);
            return Arrays.asList(ts);
        }

    }

    public static class JacksonParser implements JsonParser {

        /**
         * jackson 转换对象, 线程安全, 可以单例
         */
        public static final ObjectMapper MAPPER = new ObjectMapper();

        @Override
        public String toJson(Object obj) {
            if (obj == null) {
                return "{}";
            }
            try {
                return MAPPER.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new BaseRuntimeException("[JacksonParser] writeValueAsString 异常", e);
            }
        }

        @Override
        public String toFormatJson(Object obj) {
            if (obj == null) {
                return "{}";
            }
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new BaseRuntimeException("[JacksonParser] writeValueAsString 异常", e);
            }
        }

        @Override
        public <T> T fromJson(String json, Class<T> clazz) {
            if (json == null) {
                return null;
            }
            try {
                return MAPPER.readValue(json, clazz);
            } catch (JsonProcessingException e) {
                throw new BaseRuntimeException("[JacksonParser] fromJson 异常", e);
            }
        }

        @Override
        public <T> List<T> fromJsonArray(String json, Class<T> classOfT) {
            TypeReference<List<T>> listTypeReference = new TypeReference<List<T>>(){};
            try {
                return MAPPER.readValue(json, listTypeReference);
            } catch (JsonProcessingException e) {
                throw new BaseRuntimeException("[JacksonParser] fromJsonArray", e);
            }
        }

    }

}
