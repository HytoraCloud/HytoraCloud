
package de.lystx.hytoracloud.driver.commons.http.requests;

import de.lystx.hytoracloud.driver.commons.http.client.ClientRequest;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

@Getter
public class HttpBody extends BaseRequest {

    /**
     * The current body object
     */
    private Object body;

    public HttpBody(HttpRequest httpRequest, ClientRequest config) {
        super(httpRequest, config);
    }

    /**
     * Sets the body object of this body
     *
     * @param body the object
     * @return current body
     */
    public HttpBody setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * Gets an apache {@link HttpEntity}
     * As {@link StringEntity} with UTF-8 encoding
     *
     * @return entity
     */
    public HttpEntity getEntity() {
        return new StringEntity(body.toString(), Utils.UTF_8);
    }

    /**
     * If this body is implicit content type or not
     *
     * @return boolean
     */
    public boolean isImplicitContentType() {
        return false;
    }


}
