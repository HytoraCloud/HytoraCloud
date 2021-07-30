package de.lystx.hytoracloud.driver.commons.requests.base;

import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;

public interface IQuery<T> {

    /**
     * Sets the timeout of this request
     *
     * @param ticks the ticks (Minecraft-Ticks)
     * @param timeOutValue the value
     * @return current future
     */
    IQuery<T> setTimeOut(long ticks, T timeOutValue);

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
     * to this {@link IQuery}
     *
     * @return request
     */
    DriverRequest<T> getRequest();

}