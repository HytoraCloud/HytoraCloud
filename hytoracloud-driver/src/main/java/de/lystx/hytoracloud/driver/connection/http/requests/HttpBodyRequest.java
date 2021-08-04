
package de.lystx.hytoracloud.driver.connection.http.requests;


import de.lystx.hytoracloud.driver.connection.http.client.ClientRequest;
import de.lystx.hytoracloud.driver.connection.http.mapper.ObjectMapper;
import de.lystx.hytoracloud.driver.connection.http.mapper.ObjectMappers;

import java.util.Map;

public class HttpBodyRequest extends HttpRequest {

    /**
     * The client request
     */
    private final ClientRequest config;

    public HttpBodyRequest(ClientRequest clientRequest) {
        super(clientRequest);
        this.config = clientRequest;

    }

    //Raw override methods

    @Override
    public HttpBodyRequest contentType(RequestType contentType) {
        return (HttpBodyRequest) super.contentType(contentType);
    }

    @Override
    public HttpBodyRequest header(String name, Object obj) {
        return (HttpBodyRequest) super.header(name, obj);
    }

    @Override
    public HttpBodyRequest basicAuth(String username, String password) {
        return (HttpBodyRequest) super.basicAuth(username, password);
    }

    @Override
    public HttpBodyRequest queryString(Map<String, Object> parameters) {
        return (HttpBodyRequest) super.queryString(parameters);
    }

    @Override
    public HttpBodyRequest queryString(String name, Object value) {
        return (HttpBodyRequest) super.queryString(name, value);
    }

    /**
     * Sets the {@link HttpBody} of this request
     *
     * @param body the body
     * @return the body
     */
    public HttpBody body(String body) {
        HttpBody b = new HttpBody(this, config).setBody(body);
        this.body = b;
        return b;
    }

    /**
     * Sets the {@link HttpBody} of this request
     *
     * @param body the body object
     * @return the body
     */
    public HttpBody body(Object body) {
        RequestType requestType = this.getContentType();
        ObjectMapper mapper = ObjectMappers.getMapper(requestType);

        return body(mapper.write(body));
    }

}