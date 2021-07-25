
package de.lystx.hytoracloud.driver.commons.http.utils;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.io.InputStream;

public class HttpStreamResponse<T> extends HttpResponse<T> {

    /**
     * The request base
     */
    private final HttpRequestBase request;

    public HttpStreamResponse(org.apache.http.HttpResponse response, Class<T> responseClass, HttpRequestBase request) {
        super(response, responseClass);
        this.request = request;
    }

    @Override
    public T getBody() {
        return (T) super.rawBody;
    }

    @Override
    protected InputStream toStream(org.apache.http.HttpResponse response) {
        try {
            return response.getEntity().getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
       super.close();
       request.releaseConnection();
    }
}
