package com.github.cosycode.ext.se.json;

import com.google.gson.JsonElement;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/2/10
 * </p>
 *
 * @author pengfchen
 * @since
 **/
public abstract class JsonHelper {

    public static JsonNode parse(String json) {
        return JsonNode.GsonNode.parse(json);
    }

    public static JsonNode parse(JsonElement element) {
        return JsonNode.GsonNode.geneGsonNode(element);
    }

    public static void main(String[] args) {
        JsonNode parse = JsonHelper.parse("{\"limits\":{\"cpu\":\"8000m\",\"cpu1\":null,\"memory\":\"16384Mi\"},\"requests\":{\"cpu\":\"3076.923m\",\"memory\":\"12603.077Mi\"}}");
        JsonNode expression = parse.getExpression("limits.cpu1");
        System.out.println(expression);
    }

}
