package de.lystx.hytoracloud.driver.commons.requests.base;

import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;

public interface DriverRequestFuture<T> {


    /**
     * Adds a completion-listener to this future
     *
     * @param listener the listener
     * @return current future
     */
    DriverRequestFuture<T> addCompletionListener(DriverRequestListener<T, Void> listener);

    /**
     * Adds a completion-listener to this future
     *
     * @param listener the listener
     * @return current future
     */
    DriverRequestFuture<T> addFailureListener(DriverRequestListener<T, Exception> listener);

    /**
     * Sets the timeout of this request
     *
     * @param ticks the ticks (Minecraft-Ticks)
     * @return current future
     */
    DriverRequestFuture<T> setTimeOut(long ticks);

    /**
     * Sets the value thats gonna be returned if the timeout
     * of this request is set and reached
     *
     * @param value the value
     * @return current future
     */
    DriverRequestFuture<T> setTimeOutValue(T value);

    /**
     * Checks if the {@link DriverRequest} was successful
     * If not completed yet it will return false
     *
     * @return boolean
     */
    boolean isSuccess();

    /**
     * Checks if the {@link DriverRequest} has been completed
     *
     * @return boolean
     */
    boolean isCompleted();

    /**
     * If there was an {@link DriverRequestException} in the request
     * it will be returned otherwise it will return null
     *
     * @return error
     */
    DriverRequestException getError();

    /**
     * This pulls the response and stops the current thread until
     * a value is returned so the thread can go on an will be started again
     *
     * @return The response to this request
     * @throws DriverRequestException if api is not received or send back
     */
    T pullValue() throws DriverRequestException;

    /**
     * Gets the {@link DriverRequest} that belongs
     * to this {@link DriverRequestFuture}
     *
     * @return request
     */
    DriverRequest<T> getRequest();

}