package de.lystx.hytoracloud.driver.commons.http.mapper.impl;

import de.lystx.hytoracloud.driver.commons.http.mapper.ObjectMapper;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.utils.Utils;

public class JsonMapper implements ObjectMapper {

    @Override
    public <T> T read(String value, Class<T> valueType) {
        try {
            return JsonDocument.GSON.fromJson(value, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse " + value, e);
        }
    }

    @Override
    public String write(Object value) {
        try {
            return JsonDocument.GSON.toJson(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create json of " + value, e);
        }

    }
}
