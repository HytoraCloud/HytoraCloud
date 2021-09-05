package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future.NettyFuture;
import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;

public interface NettyComponent<T> {

    static <V> NettyComponent<V> request(Class<V> vClass) {
        return new SimpleComponent<>();
    }

    /**
     * The document data
     */
    JsonObject<?> getDocument();

    /**
     * Creates a response for this request
     *
     * @param vClass the class type
     * @param <V> the gener c
     * @return response
     */
    <V> NettyComponent<V> createResponse(Class<V> vClass);

    /**
     * If this component is a response to something
     */
    boolean isResponse();

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
    NettyComponent<T> typeClass(Class<?> typeClass);

    /**
     * The id of the request of the response
     *
     * @return string-id
     */
    String getId();

    /**
     * The target of this component
     */
    Identifiable getTarget();

    /**
     * Gets the data of this response
     *
     * @return data
     */
    T getData();

    /**
     * Queries this request
     */
    NettyFuture<T> queryRequest(INetworkConnection networkConnection);

    /**
     * Gets the error of this response if not null
     *
     * @return exception
     */
    Throwable getException();

    /**
     * The time this component took
     *
     * @return long ms
     */
    long getCompletionTimeMillis();

    /**
     * The key identifier of this component
     */
    String getKey();

    /**
     * If component was successful
     */
    boolean isSuccess();

    /**
     * Sets the id of this response
     *
     * @param id the id
     * @return current response
     */
    NettyComponent<T> id(String id);

    /**
     * Sets the key of this response
     *
     * @param key the key
     * @return current response
     */
    NettyComponent<T> key(String key);

    /**
     * Sets the document of this response
     *
     * @param document the document
     * @return current response
     */
    NettyComponent<T> document(JsonObject<?> document);

    /**
     * Sets the data of this component
     * @param t the data
     * @return current component
     */
    NettyComponent<T> value(T t);

    /**
     * Sets the error of this response
     *
     * @param throwable the error
     * @return current response
     */
    NettyComponent<T> exception(Throwable throwable);

    /**
     * Sets the success-state of this response
     *
     * @param success the state
     * @return current response
     */
    NettyComponent<T> success(boolean success);

    /**
     * Sends this response
     */
    void post(INetworkConnection connection);
}
