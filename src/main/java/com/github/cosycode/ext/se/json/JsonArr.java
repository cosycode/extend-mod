package com.github.cosycode.ext.se.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/2/21
 * </p>
 *
 * @author pengfchen
 * @since 0.2.3
 **/
public interface JsonArr extends JsonNode {

    GsonNode get(int i);

    boolean isEmpty();

    class GsonArr extends GsonNode implements JsonArr {

        private final JsonArray jsonArray;

        public GsonArr(JsonArray jsonArray) {
            this.jsonArray = jsonArray;
        }

        @Override
        public JsonElement getJsonElement() {
            return jsonArray;
        }

        @Override
        public GsonNode get(int i) {
            JsonElement jsonElement = jsonArray.get(i);
            return geneGsonNode(jsonElement);
        }

        @Override
        public boolean isEmpty() {
            return jsonArray.isEmpty();
        }

    }

}
