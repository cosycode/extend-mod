package com.github.cosycode.ext.se.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
public class JsonUtils {

    private static final JsonParser jsonParser = new GsonParser();

    private static JsonParser getJsonParser() {
        return jsonParser;
    }

    public static String toJson(Object obj) {
        return getJsonParser().toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getJsonParser().fromJson(json, classOfT);
    }

    public static <T> List<T> fromJsonArray(String json, Class<T> classOfT) {
        return getJsonParser().fromJsonArray(json, classOfT);
    }

    public static <T> String processJson(String string, Class<T> tClass, UnaryOperator<T> operator) {
        T before = fromJson(string, tClass);
        T after = operator.apply(before);
        if (after == null) {
            return null;
        }
        return toJson(after);
    }

    static abstract class JsonParser {

        abstract String toJson(Object obj);

        abstract <T> T fromJson(String json, Class<T> classOfT);

        abstract <T> List<T> fromJsonArray(String json, Class<T> classOfT);

    }

    public static class GsonParser extends JsonParser {

        @Override
        String toJson(Object obj) {
            return new Gson().toJson(obj);
        }

        @Override
        <T> T fromJson(String json, Class<T> classOfT) {
            return new Gson().fromJson(json, classOfT);
        }

        @Override
        <T> List<T> fromJsonArray(String json, Class<T> classOfT) {
            return new Gson().fromJson(json, new TypeToken<List<T>>(){}.getType());
        }

    }

    public static class JacksonParser extends JsonParser{

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
        <T> List<T> fromJsonArray(String json, Class<T> classOfT) {
            TypeReference<List<T>> listTypeReference = new TypeReference<List<T>>(){};
            try {
                return mapper.readValue(json, listTypeReference);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("fromJsonArray", e);
            }
        }

    }
}
