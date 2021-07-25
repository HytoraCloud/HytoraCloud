
package de.lystx.hytoracloud.driver.commons.http.utils;


public interface HttpCallback<T> {

    /**
     * When the callback is completed
     *
     * @param response the response
     */
    void completed(HttpResponse<T> response);

    /**
     * When the callback failed
     *
     * @param e the exception
     */
    void failed(Exception e);

    /**
     * When cancelled
     */
    void cancelled();
}
