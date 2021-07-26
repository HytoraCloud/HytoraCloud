package de.lystx.hytoracloud.driver.commons.http.listener;

import de.lystx.hytoracloud.driver.commons.http.utils.HttpRequestType;

public interface IListener {

    /**
     * The path of this request
     *
     * @return path name
     */
    String path();

    /**
     * Handles this request
     *
     * @param context the context
     */
    IResponse handle(HttpRequestType type, IContext context);

}
