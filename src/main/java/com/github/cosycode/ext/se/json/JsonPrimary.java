package com.github.cosycode.ext.se.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/2/21
 * </p>
 *
 * @author pengfchen
 * @since 0.2.3
 **/
public interface JsonPrimary extends JsonNode {

    GsonPrimary deepCopy();

    boolean isBoolean();

    boolean getAsBoolean();

    boolean isNumber();

    Number getAsNumber();

    boolean isString();

    double getAsDouble();

    BigDecimal getAsBigDecimal();

    BigInteger getAsBigInteger();

    float getAsFloat();

    long getAsLong();

    short getAsShort();

    int getAsInt();

    byte getAsByte();

    class GsonPrimary extends GsonNode implements JsonPrimary {

        private final JsonPrimitive jsonPrimitive;

        public GsonPrimary(JsonPrimitive jsonPrimitive) {
            this.jsonPrimitive = jsonPrimitive;
        }

        @Override
        public JsonElement getJsonElement() {
            return jsonPrimitive;
        }

        public GsonPrimary(Boolean bool) {
            jsonPrimitive = new JsonPrimitive(bool);
        }

        public GsonPrimary(Number number) {
            jsonPrimitive = new JsonPrimitive(number);
        }

        public GsonPrimary(String string) {
            jsonPrimitive = new JsonPrimitive(string);
        }

        public GsonPrimary(Character c) {
            jsonPrimitive = new JsonPrimitive(c);
        }

        @Override
        public GsonPrimary deepCopy() {
            return this;
        }

        @Override
        public boolean isBoolean() {
            return jsonPrimitive.isBoolean();
        }

        @Override
        public boolean getAsBoolean() {
            return jsonPrimitive.getAsBoolean();
        }

        @Override
        public boolean isNumber() {
            return jsonPrimitive.isNumber();
        }

        @Override
        public Number getAsNumber() {
            return jsonPrimitive.getAsNumber();
        }

        @Override
        public boolean isString() {
            return jsonPrimitive.isString();
        }

        @Override
        public String getAsString() {
            return jsonPrimitive.getAsString();
        }

        @Override
        public double getAsDouble() {
            return jsonPrimitive.getAsDouble();
        }

        @Override
        public BigDecimal getAsBigDecimal() {
            return jsonPrimitive.getAsBigDecimal();
        }

        @Override
        public BigInteger getAsBigInteger() {
            return jsonPrimitive.getAsBigInteger();
        }

        @Override
        public float getAsFloat() {
            return jsonPrimitive.getAsFloat();
        }

        @Override
        public long getAsLong() {
            return jsonPrimitive.getAsLong();
        }

        @Override
        public short getAsShort() {
            return jsonPrimitive.getAsShort();
        }

        @Override
        public int getAsInt() {
            return jsonPrimitive.getAsInt();
        }

        @Override
        public byte getAsByte() {
            return jsonPrimitive.getAsByte();
        }

        @Override
        public int hashCode() {
            return jsonPrimitive.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return jsonPrimitive.equals(obj);
        }

    }

}
