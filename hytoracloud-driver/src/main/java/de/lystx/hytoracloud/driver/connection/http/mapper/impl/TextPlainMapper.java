package de.lystx.hytoracloud.driver.connection.http.mapper.impl;

import de.lystx.hytoracloud.driver.connection.http.mapper.ObjectMapper;

public class TextPlainMapper implements ObjectMapper {

    @Override
    public <T> T read(String value, Class<T> valueType) {
        throw new UnsupportedOperationException("Cannot convert text content to type: " + valueType.getSimpleName());
    }

    @Override
    public String write(Object value) {
        return String.valueOf(value);
    }
}
