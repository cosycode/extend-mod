package com.github.cosycode.ext.se.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Set;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/2/21
 * </p>
 *
 * @author pengfchen
 * @since 0.2.3
 **/
public interface JsonObj extends JsonNode {

    Set<String> keySet();

    int size();

    class GsonObj extends GsonNode implements JsonObj {

        private final JsonObject jsonObject;

        public GsonObj(JsonObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public JsonElement getJsonElement() {
            return jsonObject;
        }

        @Override
        public Set<String> keySet() {
            return jsonObject.keySet();
        }

        @Override
        public int size() {
            return jsonObject.size();
        }

    }

}
