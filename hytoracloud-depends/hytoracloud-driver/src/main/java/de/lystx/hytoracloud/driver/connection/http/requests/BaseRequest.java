
package de.lystx.hytoracloud.driver.connection.http.requests;

import com.google.gson.reflect.TypeToken;
import de.lystx.hytoracloud.driver.connection.http.client.ClientRequest;
import de.lystx.hytoracloud.driver.connection.http.utils.HttpResponse;
import de.lystx.hytoracloud.driver.connection.http.utils.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * The abstract class for
 * every request instance
 */

@Getter @AllArgsConstructor
public abstract class BaseRequest {

    /**
     * The http request base object
     */
    protected HttpRequest httpRequest;

    /**
     * The client request object
     */
    protected final ClientRequest clientRequest;

    /**
     * Gets this requests response as {@link String}
     *
     * @return returned response value
     */
    public HttpResponse<String> asString() {
        return clientRequest.loadResponse(httpRequest, String.class);
    }

    /**
     * Gets this requests response as {@link JsonNode}
     *
     * @return returned response value
     */
    public HttpResponse<JsonNode> asJson() {
        return clientRequest.loadResponse(httpRequest, JsonNode.class);
    }

    /**
     * Gets this requests response as {@link Object}
     *
     * @return returned response value
     */
    public <T> HttpResponse<T> asObject(Class<T> responseClass) {
        return clientRequest.loadResponse(httpRequest, responseClass);
    }

    /**
     * Gets this requests response as {@link List}
     *
     * @return returned response value
     */
    public <T> HttpResponse<List<T>> asList(Class<T> responseClass) {
        Class<? super List<T>> rawType = new TypeToken<List<T>>() {}.getRawType();
        return (HttpResponse<List<T>>) clientRequest.loadResponse(httpRequest, rawType);
    }

}
