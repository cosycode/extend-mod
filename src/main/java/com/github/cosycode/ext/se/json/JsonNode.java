package com.github.cosycode.ext.se.json;

import com.github.cosycode.common.ext.bean.DoubleBean;
import com.github.cosycode.common.lang.BaseRuntimeException;
import com.github.cosycode.common.lang.NotSupportException;
import com.github.cosycode.common.lang.ShouldNotHappenException;
import com.github.cosycode.common.util.common.CollectUtils;
import com.google.gson.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/2/21
 * </p>
 *
 * @author pengfchen
 * @since 0.2.3
 **/
public interface JsonNode {

    JsonNode getExpression(String expression);

    String getAsString();

    default boolean isJsonArr() {
        return this instanceof JsonArr;
    }

    default boolean isJsonObj() {
        return this instanceof JsonObj;
    }

    default boolean isJsonPrimary() {
        return this instanceof JsonPrimary;
    }

    default boolean isJsonNul() {
        return this instanceof JsonNul;
    }

    default JsonObj getAsJsonObj() {
        if (this.isJsonObj()) {
            return (JsonObj)this;
        } else {
            throw new IllegalStateException("Not a JSON Object: " + this);
        }
    }

    default JsonArr getAsJsonArr() {
        if (this.isJsonArr()) {
            return (JsonArr) this;
        } else {
            throw new IllegalStateException("Not a JSON Array: " + this);
        }
    }

    default JsonPrimary getAsJsonPrimary() {
        if (this.isJsonPrimary()) {
            return (JsonPrimary)this;
        } else {
            throw new IllegalStateException("Not a JSON Primary: " + this);
        }
    }

    default JsonNul getAsJsonNul() {
        if (this.isJsonNul()) {
            return (JsonNul)this;
        } else {
            throw new IllegalStateException("Not a JSON Null: " + this);
        }
    }

    default JsonArr getAsJsonArr(String expression) {
        JsonNode jsonNode = getExpression(expression);
        return jsonNode.getAsJsonArr();
    }

    default JsonObj getAsJsonObj(String expression) {
        JsonNode jsonNode = getExpression(expression);
        return jsonNode.getAsJsonObj();
    }

    default JsonPrimary getAsPrimary(String expression) {
        JsonNode jsonNode = getExpression(expression);
        return jsonNode.getAsJsonPrimary();
    }

    default JsonNul getAsJsonNul(String expression) {
        JsonNode jsonNode = getExpression(expression);
        return jsonNode.getAsJsonNul();
    }

    abstract class GsonNode implements JsonNode {

        public static GsonNode parse(String json) {
            JsonElement jsonElement = JsonParser.parseString(json);
            return geneGsonNode(jsonElement);
        }

        public static GsonNode geneGsonNode(JsonElement jsonElement) {
            if (jsonElement == null) {
                return null;
            }
            if (jsonElement.isJsonObject()) {
                return new JsonObj.GsonObj(jsonElement.getAsJsonObject());
            }
            if (jsonElement.isJsonArray()) {
                return new JsonArr.GsonArr(jsonElement.getAsJsonArray());
            }
            if (jsonElement.isJsonPrimitive()) {
                return new JsonPrimary.GsonPrimary(jsonElement.getAsJsonPrimitive());
            }
            if (jsonElement.isJsonNull()) {
                return new JsonNul.GsonNul();
            }
            throw new ShouldNotHappenException();
        }

        public abstract JsonElement getJsonElement();

        @Override
        public JsonNode getExpression(String expression) {
            JsonElement jsonElement0 = getJsonElement(getJsonElement(), expression);
            return geneGsonNode(jsonElement0);
        }

        @Override
        public String getAsString() {
            return getJsonElement().getAsString();
        }

        @Override
        public String toString() {
            return getJsonElement().toString();
        }

        public static List<DoubleBean<String, String>> splitExpression(String expression) {
            // check trim 后均为非空白字符
            if (! Pattern.matches("\\S+", expression)) {
                throw new IllegalArgumentException("the characters in expression can not be space char");
            }
            if (!expression.startsWith("[")) {
                expression = "." + expression.trim();
            }
            int len = expression.length();
            if (len <= 1) {
                throw new IllegalArgumentException("invalid expression: " + expression);
            }
            // 代理转换字符串
            String tempExpression = expression.replaceAll("\\\\.", "\1\2");

            List<DoubleBean<String, String>> list = new ArrayList<>();
            int idx = 0;
            while (idx < len) {
                char c = tempExpression.charAt(idx);
                if (c == '[') {
                    if (tempExpression.charAt(idx + 1) == ']') {
                        throw new BaseRuntimeException("分段为空 [] " + expression);
                    }
                    // array
                    int leftCnt = 1;
                    int i = idx + 1;
                    for (; i < len; i++) {
                        char ch = tempExpression.charAt(i);
                        if (ch == ']') {
                            leftCnt--;
                            if (leftCnt <= 0) {
                                list.add(DoubleBean.of(Array.class.getSimpleName(), expression.substring(idx + 1, i)));
                                idx = i + 1;
                                break;
                            }
                        } else if (ch == '[') {
                            leftCnt++;
                        }
                    }
                    if (i >= len) {
                        throw new BaseRuntimeException("解析数组失败: expression: {}, successPart: {}, fail: {}", expression, list, expression.substring(idx));
                    }
                    continue;
                }
                if (c != '.') {
                    throw new BaseRuntimeException("分段首位不为 . 也不为 [, 解析复杂key失败: expression: {}, successPart: {}, fail: {}", expression, list, expression.substring(idx));
                }
                idx++;
                if (idx >= len) {
                    throw new BaseRuntimeException("末尾为点");
                }
                c = tempExpression.charAt(idx);
                if (c == '"') {
                    if (tempExpression.charAt(idx + 1) == '"') {
                        throw new BaseRuntimeException("分段为空 \"\" " + expression);
                    }
                    // object "p1.p2.p3"
                    int tmp = tempExpression.indexOf('"', idx + 1);
                    if (tmp < 0) {
                        throw new BaseRuntimeException("解析复杂key失败: expression: {}, successPart: {}, fail: {}", expression, list, expression.substring(idx));
                    }
                    list.add(DoubleBean.of(Object.class.getSimpleName(), expression.substring(idx + 1, tmp)));
                    idx = tmp + 1;
                } else {
                    // object "p1.p2.p3", 首位不应该为 .
                    if (c == '.' || c == '[') {
                        throw new BaseRuntimeException("分段解析失败, 发现两个连续的.. ., expression: {}, successPart: {}, fail: {}", expression, list, expression.substring(idx));
                    }
                    int tmpP = tempExpression.indexOf('.', idx);
                    int tmpL = tempExpression.indexOf("[", idx);
                    if (tmpP < 0) {
                        if (tmpL < 0) {
                            list.add(DoubleBean.of(Object.class.getSimpleName(), expression.substring(idx, expression.length())));
                            idx = expression.length();
                        } else {
                            list.add(DoubleBean.of(Object.class.getSimpleName(), expression.substring(idx, tmpL)));
                            idx = tmpL;
                        }
                    } else {
                        if (tmpL < 0) {
                            list.add(DoubleBean.of(Object.class.getSimpleName(), expression.substring(idx, tmpP)));
                            idx = tmpP;
                        } else {
                            int min = Math.min(tmpL, tmpP);
                            list.add(DoubleBean.of(Object.class.getSimpleName(), expression.substring(idx, min)));
                            idx = min;
                        }
                    }
                }
            }
            if (idx != len) {
                throw new BaseRuntimeException("解析表达式失败: expression: {}, successPart: {}", expression, list);
            }
            return list;
        }

        /**
         * <p>
         * expression: 对象查询, 以 . 作为分隔, 如果 key 值里面有点, 则可以使用 \. 表示.
         * 数组查询有两种情况, 1, 查询数组里面的第n 个对象, 可以使用 [n] 表示.
         * 若是数组查询有筛选情况, 则可以使用 [key=value] 的形式, value 为对象的toString 输出形式.
         * <br/><b>case1: 普通查询</b> p1.p2.p3
         * <br/><b>case2: </b> p1.[4].p3
         * <br/><b>case3: </b> p1.[name=pfc].p3
         * <br/><b>case4: </b> p1.\\.p3
         *
         * </p>
         *
         * @param jsonElement json element 对象
         * @param expression  查询表达式
         * @return json element 对象
         */
        public static JsonElement getJsonElement(JsonElement jsonElement, @NonNull String expression) {
            if (expression.contains("\\\\")) {
                throw new IllegalArgumentException("expression can't contain two slashes ==> " + expression);
            }
            List<DoubleBean<String, String>> doubleBeans = splitExpression(expression);
            for (DoubleBean<String, String> doubleBean : doubleBeans) {
                if (jsonElement == null) {
                    return null;
                }
                String type = doubleBean.getO1();
                String numberString = doubleBean.getO2();
                if (type.equals(Array.class.getSimpleName())) {
                    if (StringUtils.isNumeric(numberString)) {
                        int idx = Integer.parseInt(numberString);
                        jsonElement = jsonElement.getAsJsonArray().get(idx);
                    } else if (numberString.contains("=")) {
                        // 键值对
                        String[] kvs = numberString.split(",");
                        JsonArray asJsonArray = jsonElement.getAsJsonArray();
                        List<JsonElement> collect = asJsonArray.asList().stream().filter(it -> {
                            JsonObject asJsonObject = it.getAsJsonObject();
                            if (asJsonObject == null) {
                                return false;
                            }
                            for (String kv : kvs) {
                                String[] kAndV = kv.split("=");
                                JsonElement value = asJsonObject.get(kAndV[0]);
                                if (value == null) {
                                    return false;
                                }
                                if (! value.getAsString().equals(kAndV[1])) {
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                        jsonElement = CollectUtils.listToOneWithThrow(collect);
                    }
                } else if (type.equals(Object.class.getSimpleName())) {
                    jsonElement = jsonElement.getAsJsonObject().get(numberString);
                } else {
                    throw new NotSupportException();
                }
            }
            return jsonElement;
        }
    }

}
