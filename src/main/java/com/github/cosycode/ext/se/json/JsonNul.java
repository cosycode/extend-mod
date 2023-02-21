package com.github.cosycode.ext.se.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/2/21
 * </p>
 *
 * @author pengfchen
 * @since 0.2.3
 **/
public interface JsonNul extends JsonNode {

    class GsonNul extends GsonNode implements JsonNul {
        @Override
        public JsonElement getJsonElement() {
            return JsonNull.INSTANCE;
        }
    }
}
