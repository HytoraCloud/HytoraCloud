
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * A callback for accepting the results of a {@link CloudFlareResponse}
 * computation asynchronously OR synchronously.
 */
public interface CloudflareCallback<V> {
    /**
     * Invoked with the result of the request being successful.
     */
    void onSuccess( V response );
    
    /**
     * Invoked when a request fails or is canceled.
     * <p>
     * <p>If this method is in "async-mode" and the future's {@link Future#get() get} method throws an {@link ExecutionException}, then
     * the cause is passed to this method.
     *
     * Any other thrown object is passed unaltered.
     */
    void onFailure( Throwable t, int statusCode, String statusMessage, Map<Integer, String> errors );
}
