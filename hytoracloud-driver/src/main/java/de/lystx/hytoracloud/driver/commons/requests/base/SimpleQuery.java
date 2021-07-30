package de.lystx.hytoracloud.driver.commons.requests.base;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

@Getter
public class SimpleQuery<T> implements IQuery<T> {

    private static final long serialVersionUID = -4424743782381022342L;

    /**
     * The request
     */
    private DriverRequest<T> request;

    /**
     * The response
     */
    private DriverResponse<?> driverResponse;

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

    public SimpleQuery(String typeDummy, T dummy) {
        this(new DriverRequestObject<>(typeDummy));

        this.dummyObject = dummy;
    }

    public SimpleQuery(DriverRequest<T> request) {
        this.request = request;
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
    public IQuery<T> setTimeOut(long timeOut, T timeOutValue) {
        this.timeOut = timeOut;
        this.timeOutValue = timeOutValue;
        return this;
    }

    /**
     * Completes this request and handles all listeners
     *
     * @param response the response
     */
    public void completeFuture(DriverResponse<?> response) {

        this.driverResponse = response;
        this.completed = true;
        this.success = response.isSuccess();

        if (response.isSuccess()) {
            this.response = (T) response.getData();
        } else {
            this.error = response.getError();
        }
        for (CountDownLatch latch : countDownLatches) {
            latch.countDown();
        }
        this.countDownLatches.clear();
    }


    public void setRequest(DriverRequest<?> request) {
        this.request = (DriverRequest<T>) request;
    }

}
