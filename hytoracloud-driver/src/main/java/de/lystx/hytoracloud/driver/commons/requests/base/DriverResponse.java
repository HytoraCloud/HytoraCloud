package de.lystx.hytoracloud.driver.commons.requests.base;

import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;

public interface DriverResponse<T> {

    /**
     * Sets the data of this response
     *
     * @param data the data
     * @return current response
     */
    DriverResponse<T> data(Object data);

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
    DriverResponse<T> typeClass(Class<?> typeClass);

    /**
     * Checks if response is successful
     *
     * @return boolean
     */
    boolean isSuccess();

    /**
     * The id of the request of the response
     *
     * @return string-id
     */
    String getId();

    /**
     * Gets the data of this response
     *
     * @return data
     */
    T getData();

    /**
     * Gets the error of this response if not null
     *
     * @return exception
     */
    DriverRequestException getError();

    /**
     * Sets the id of this response
     *
     * @param id the id
     * @return current response
     */
    DriverResponse<T> id(String id);

    /**
     * Sets the error of this response
     *
     * @param error the error
     * @return current response
     */
    DriverResponse<T> error(DriverRequestException error);

    /**
     * Sets the success-state of this response
     *
     * @param success the state
     * @return current response
     */
    DriverResponse<T> success(boolean success);

    /**
     * Sends this response
     */
    void send();
}
