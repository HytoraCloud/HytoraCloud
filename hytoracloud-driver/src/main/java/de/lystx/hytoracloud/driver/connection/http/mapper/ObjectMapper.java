package de.lystx.hytoracloud.driver.connection.http.mapper;

public interface ObjectMapper {

    /**
     * Writes a given object into json string
     *
     * @param value the object
     * @return json string
     */
    String write(Object value);

    /**
     * Reads a specific object from a string
     *
     * @param value the json string
     * @param valueType the class type
     * @param <T> the generic
     * @return object
     */
    <T> T read(String value, Class<T> valueType);

}
