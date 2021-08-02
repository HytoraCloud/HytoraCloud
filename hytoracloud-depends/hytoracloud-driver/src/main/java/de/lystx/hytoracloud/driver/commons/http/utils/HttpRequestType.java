
package de.lystx.hytoracloud.driver.commons.http.utils;

public enum HttpRequestType {

    /**
     * Gets something
     */
    GET,

    /**
     * Posts something (creating)
     */
    POST,

    /**
     * Updates something
     */
    PUT,

    /**
     * Deletes something
     */
    DELETE,

    /**
     * Patches something
     */
    PATCH,

    /**
     * Just for headers
     */
    HEAD,

    /**
     * Just for options
     */
    OPTIONS
}
