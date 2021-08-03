
package de.lystx.hytoracloud.driver.connection.http.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonNode {

    private JsonObject jsonObject;
    private JsonArray jsonArray;

    private boolean array;

    public JsonNode(String json) {
        if (json == null || "".equals(json.trim())) {
            jsonObject = new JsonObject();
        } else {
            try {
                jsonObject = (JsonObject) new JsonParser().parse(json);
            } catch (Exception e) {
                // It may be an array
                jsonArray = (JsonArray) new JsonParser().parse(json);
                array = true;
            }
        }
    }


    public boolean isArray() {
        return this.array;
    }

    @Override
    public String toString() {
        if (isArray()) {
            if (jsonArray == null)
                return null;
            return jsonArray.toString();
        }
        if (jsonObject == null)
            return "";
        return jsonObject.toString();
    }
}
