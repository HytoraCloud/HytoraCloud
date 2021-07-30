package de.lystx.hytoracloud.driver.commons.requests.base;

public interface IQueryListener<T, OTHER> {

    /**
     * Handles this listener
     *
     * @param response the response (might be null)
     * @param other the other generic parameter
     */
    void handle(T response, OTHER other);
}
