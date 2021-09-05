package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component.NettyComponent;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component.SimpleComponent;
import de.lystx.hytoracloud.driver.utils.CloudDriverException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Getter
public class SimpleFuture<T> implements NettyFuture<T> {

    private static final long serialVersionUID = -4424743782381022342L;

    /**
     * The request
     */
    private NettyComponent<T> request;

    /**
     * The response
     */
    private NettyComponent<?> nettyResponse;

    /**
     * The latches to lock and unlock
     */
    private final Collection<CountDownLatch> countDownLatches;

    /**
     * All listeners
     */
    private final List<NettyFutureListener<T>> listeners;

    /**
     * The response if set
     */
    private volatile T response;

    /**
     * The error
     */
    private volatile Throwable error;

    /**
     * If completed yet
     */
    private volatile boolean completed;

    /**
     * If the future was successful
     */
    private boolean success;

    /**
     * The time the future took
     */
    @Setter
    private long completionTimeMillis = -1L;

    /**
     * If already executed
     */
    private boolean executed;

    public SimpleFuture(NettyComponent<T> request) {
        this.request = request;
        this.countDownLatches = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    @Override
    public NettyFuture<T> nonBlocking(T blockingObject) {

        SimpleComponent<T> simpleComponent = new SimpleComponent<>();
        simpleComponent.id(this.request.getId());
        simpleComponent.data(blockingObject);
        simpleComponent.success(true);

        this.completeFuture(simpleComponent);
        return this;
    }

    @Override
    public NettyFuture<T> addListener(NettyFutureListener<T> listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    public T pullValue() throws CloudDriverException {
        this.executed = true;
        if (success) {
            return response;
        }
        CountDownLatch latch = new CountDownLatch(1);
        this.countDownLatches.add(latch);

        while (latch.getCount() > 0) {
            try {
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (success) {
            return response;
        }
        throw getError() == null ? new CloudDriverException("The request timed out and no error was provided!") : new CloudDriverException(getError());
    }

    /**
     * Completes this request and handles all listeners
     *
     * @param response the response
     */
    public void completeFuture(NettyComponent<?> response) {

        if (this.completed) {
            return;
        }

        this.nettyResponse = response;
        this.completed = true;
        this.success = response.isSuccess();
        this.completionTimeMillis = response.getCompletionTimeMillis();

        if (response.isSuccess()) {
            this.response = (T) response.getData();
        } else {
            this.error = response.getException();
        }

        for (NettyFutureListener<T> listener : this.listeners) {
            listener.handle(this);
        }

        for (CountDownLatch latch : countDownLatches) {
            latch.countDown();
        }
        this.countDownLatches.clear();
    }

    public void setRequest(NettyComponent<?> request) {
        this.request = (NettyComponent<T>) request;
    }

}
