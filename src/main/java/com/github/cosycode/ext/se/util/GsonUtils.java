package com.github.cosycode.ext.se.util;

import com.github.cosycode.common.util.otr.PrintTool;
import com.google.gson.JsonElement;
import lombok.NonNull;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/12/8
 * </p>
 *
 * @author pengfchen
 * @since 0.2.2
 **/
public class GsonUtils {

    public static JsonElement getJsonElement(JsonElement jsonElement, @NonNull String expression) {
        String[] split = expression.split("\\.");
        for (String s : split) {
            if (s.startsWith("[]")) {
                int idx = Integer.parseInt(s.substring(2));
                jsonElement = jsonElement.getAsJsonArray().get(idx);
            } else if (s.startsWith("{}")) {
                String substring = s.substring(2);
                jsonElement = jsonElement.getAsJsonObject().get(substring);
            } else {
                throw new IllegalArgumentException(PrintTool.format("not support the expression: {}, subExpression: {}, json:{}", s, expression, jsonElement));
            }
        }
        return jsonElement;
    }

}
