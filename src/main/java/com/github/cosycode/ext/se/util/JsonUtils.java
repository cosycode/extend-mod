package com.github.cosycode.ext.se.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
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
            return new Gson().fromJson(json, new TypeToken<List<T>>(){}.getType());
        }

    }

    public static class JacksonParser implements JsonParser {

        /**
         * jackson 转换对象, 线程安全, 可以单例
         */
        public static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String toJson(Object obj) {
            if (obj == null) {
                return "{}";
            }
            try {
                return mapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("writeValueAsString 异常", e);
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
                throw new RuntimeException("writeValueAsString 异常", e);
            }
        }

        @Override
        public <T> T fromJson(String json, Class<T> clazz) {
            if (json == null) {
                return null;
            }
            try {
                return mapper.readValue(json, clazz);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("fromJson 异常", e);
            }
        }

        @Override
        public <T> List<T> fromJsonArray(String json, Class<T> classOfT) {
            TypeReference<List<T>> listTypeReference = new TypeReference<List<T>>(){};
            try {
                return mapper.readValue(json, listTypeReference);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("fromJsonArray", e);
            }
        }

    }
}
