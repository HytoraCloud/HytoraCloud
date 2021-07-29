package de.lystx.hytoracloud.driver.commons.requests.base;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;
import de.lystx.hytoracloud.driver.commons.wrapped.WrappedObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

@Getter
public class DriverFutureObject<T> implements DriverRequestFuture<T> {

    private static final long serialVersionUID = -4424743782381022342L;

    /**
     * The request
     */
    private DriverRequest<T> request;

    public void setRequest(DriverRequest<?> request) {
        this.request = (DriverRequest<T>) request;
    }

    /**
     * All listeners for completion
     */
    private final Collection<DriverRequestListener<T, Void>> completionListeners;

    /**
     * All listeners for failure
     */
    private final Collection<DriverRequestListener<T, Exception>> failureListeners;

    /**
     * The latches to lock and unlock
     */
    private final Collection<CountDownLatch> countDownLatches;

    /**
     * The response if set
     */
    private volatile T response;

    /**
     * The error
     */
    private volatile DriverRequestException error;

    /**
     * If completed yet
     */
    private volatile boolean completed;

    /**
     * If the future was successful
     */
    private boolean success;

    /**
     * The timeout
     */
    private long timeOut;

    /**
     * The dummy object
     */
    private T dummyObject;

    /**
     * The value if timeout is reached
     */
    private T timeOutValue;

    public DriverFutureObject(String typeDummy, T dummy) {
        this(new DriverRequestObject<>(typeDummy));

        this.dummyObject = dummy;
    }

    public DriverFutureObject(DriverRequest<T> request) {
        this.request = request;
        this.completionListeners = new ArrayList<>();
        this.failureListeners = new ArrayList<>();
        this.countDownLatches = new ArrayList<>();
        this.timeOut = -1;
    }

    @Override
    public T pullValue() throws DriverRequestException {
        CountDownLatch latch = new CountDownLatch(1);
        this.countDownLatches.add(latch);
        if (this.dummyObject != null) {
            this.completeFuture(new DriverResponseObject<T>().id(this.request.getId()).data(this.dummyObject).success(true));
        } else {
            if (this.timeOut != -1) {
                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> this.completeFuture(new DriverResponseObject<T>().id(this.request.getId()).data(this.timeOutValue).success(false)), this.timeOut);
            }
        }
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
        throw getError();
    }

    @Override
    public DriverRequestFuture<T> setTimeOut(long timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    @Override
    public DriverRequestFuture<T> setTimeOutValue(T value) {
        this.timeOutValue = value;
        return this;
    }

    @Override
    public DriverRequestFuture<T> addCompletionListener(DriverRequestListener<T, Void> listener) {
        if (response != null) {
            listener.handle(this.response, null);
        } else {
            completionListeners.add(listener);
        }
        return this;
    }

    @Override
    public DriverRequestFuture<T> addFailureListener(DriverRequestListener<T, Exception> listener) {
        if (error != null) {
            listener.handle(this.response, this.error);
        } else {
            failureListeners.add(listener);
        }
        return this;
    }


    /**
     * Completes this request and handles all listeners
     *
     * @param response the response
     */
    public void completeFuture(DriverResponse<?> response) {

        this.completed = true;
        this.success = response.isSuccess();

        if (response.isSuccess()) {
            this.response = (T) response.getData();
            for (DriverRequestListener<T, Void> listener : completionListeners) {
                listener.handle(this.response, null);
            }
        } else {
            this.error = response.getError();
            for (DriverRequestListener<T, Exception> handler : failureListeners) {
                handler.handle(null, this.error);
            }
        }
        for (CountDownLatch latch : countDownLatches) {
            latch.countDown();
        }
        this.countDownLatches.clear();
    }

}
