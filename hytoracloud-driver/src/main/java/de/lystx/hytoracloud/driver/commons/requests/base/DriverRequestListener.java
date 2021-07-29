package de.lystx.hytoracloud.driver.commons.requests.base;

public interface DriverRequestListener<T, OTHER> {

    void handle(T response, OTHER other);
}
