package de.lystx.hytoracloud.driver.commons.requests.base;

import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;

import java.util.function.Consumer;

public interface DriverQuery<T> {

    static <T> DriverQuery<T> dummy(String key) {
        return dummy(key, null);
    }
    static <T> DriverQuery<T> dummy(String key, T value) {
        return new DriverQueryObject<>(key, value);
    }

    /**
     * Sets the timeout of this request
     *
     * @param ticks the ticks (Minecraft-Ticks)
     * @param timeOutValue the value
     * @return current future
     */
    DriverQuery<T> setTimeOut(long ticks, T timeOutValue);

    /**
     * Adds a listener to this query
     *
     * @param listener the listener
     * @return current future
     */
    DriverQuery<T> addFutureListener(Consumer<DriverQuery<T>> listener);

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
     * If there was an {@link Throwable} in the request
     * it will be returned otherwise it will return null
     *
     * @return error
     */
    Throwable getError();

    /**
     * This pulls the response and stops the current thread until
     * a value is returned so the thread can go on an will be started again
     *
     * @return The response to this request
     */
    T pullValue() throws DriverRequestException;

    /**
     * Gets the {@link DriverRequest} that belongs
     * to this {@link DriverQuery}
     *
     * @return request
     */
    DriverRequest<T> getRequest();

}