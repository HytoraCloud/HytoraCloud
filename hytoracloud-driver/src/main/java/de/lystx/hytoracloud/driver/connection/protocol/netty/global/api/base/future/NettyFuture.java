package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component.NettyComponent;
import de.lystx.hytoracloud.driver.utils.CloudDriverException;

import java.util.function.Consumer;

public interface NettyFuture<T> {

    /**
     * The time the future took to complete
     * @return long ms
     */
    long getCompletionTimeMillis();

    /**
     * Adds a listener to this query
     *
     * @param listener the listener
     * @return current future
     */
    NettyFuture<T> addListener(NettyFutureListener<T> listener);

    /**
     * Checks if the {@link NettyComponent} was successful
     * If not completed, yet it will return false
     *
     * @return boolean
     */
    boolean isSuccess();

    /**
     * Checks if the {@link NettyComponent} has been completed
     *
     * @return boolean
     */
    boolean isCompleted();

    /**
     * Marks this future as dummy
     */
    NettyFuture<T> nonBlocking(T blockingObject);

    /**
     * If there was an {@link Throwable} in the request
     * it will be returned otherwise it will return null
     *
     * @return error
     */
    Throwable getError();

    /**
     * This pulls the response and stops the current thread until
     * a value is returned so the thread can go on and will be started again
     *
     * @return The response to this request
     */
    T pullValue() throws CloudDriverException;

    /**
     * Gets the {@link NettyComponent} that belongs
     * to this {@link NettyFuture}
     *
     * @return request
     */
    NettyComponent<T> getRequest();

}