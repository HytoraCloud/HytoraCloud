package de.lystx.hytoracloud.driver.commons.requests.base;

import de.lystx.hytoracloud.driver.commons.storage.JsonObject;

public interface DriverRequest<T> {


    static <T> DriverRequest<T> create(String key) {
        return new DriverRequestObject<T>(key);
    }

    static <T> DriverRequest<T> create(String key, String target) {
        return new DriverRequestObject<T>(key).target(target);
    }

    static <T> DriverRequest<T> create(String key, JsonObject<?> document) {
        return new DriverRequestObject<T>(key, document);
    }

    static <T> DriverRequest<T> create(String key, String target, JsonObject<?> document) {
        return new DriverRequestObject<T>(key, document).target(target);
    }

    static <T> DriverRequest<T> create(String key, Class<T> typeClass) {
        return new DriverRequestObject<T>(key).typeClass(typeClass);
    }

    static <T> DriverRequest<T> create(String key, String target, Class<T> typeClass) {
        return new DriverRequestObject<T>(key).target(target).typeClass(typeClass);
    }

    static <T> DriverRequest<T> create(String key, String target, JsonObject<?> document, Class<T> typeClass) {
        return new DriverRequestObject<T>(key, document).target(target).typeClass(typeClass);
    }
    static <T> DriverRequest<T> create(String key, JsonObject<?> document, Class<T> typeClass) {
        return new DriverRequestObject<T>(key, document).typeClass(typeClass);
    }

    /**
     * Appends something to this request (Information)
     *
     * @param key the key
     * @param value the value
     * @return current request
     */
    DriverRequest<T> append(String key, Object value);

    /**
     * Appends the whole json object
     *
     * @param jsonData the data
     * @return current request
     */
    DriverRequest<T> json(JsonObject<?> jsonData);

    /**
     * The key of this request
     * Here you can provide a basic action name
     * so you can listen for it (e.g. "UPDATE_CACHE")
     *
     * @return key
     */
    String getKey();

    /**
     * Checks if the request key equals the provided
     *
     * @param key the key to check
     * @return boolean
     */
    default boolean equalsIgnoreCase(String key) {
        return this.getKey().equalsIgnoreCase(key);
    }

    /**
     * The generic-type-class
     *
     * @return class
     */
    Class<T> typeClass();

    /**
     * Sets the type class
     * @param typeClass the class
     * @return current request
     */
    DriverRequest<T> typeClass(Class<?> typeClass);

    /**
     * Submits this request
     *
     * @return future request
     */
    IQuery<T> execute();

    /**
     * The id to identify this request
     *
     * @return id as string
     */
    String getId();

    /**
     * The target of this request
     *
     * @return the target
     */
    String getTarget();

    /**
     * Sets the target
     *
     * @param target the target
     * @return current request
     */
    DriverRequest<T> target(String target);

    /**
     * Creates a {@link DriverResponseObject} for this request
     *
     * @return response builder
     */
    <V> DriverResponse<V> createResponse(Class<V> vClass);

    /**
     * Creates a {@link DriverResponseObject} for this request
     *
     * @return response builder
     */
    <V> DriverResponse<V> createResponse();

    /**
     * Gets the data of this request#
     *
     * @return the data
     */
    JsonObject<?> getDocument();
}
