package de.lystx.hytoracloud.driver.commons.http.listener;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.URI;

public interface IContext extends Serializable {

    /**
     * Creates a new {@link IResponse}
     *
     * @return response
     */
    IResponse createResponse();

    /**
     * Closes this context
     */
    void close();

    /**
     * The address of this context
     * 
     * @return address
     */
    InetSocketAddress getLocalAddress();

    Headers getRequestHeaders();

    Headers getResponseHeaders();

    URI getRequestURI();

    String getRequestMethod();

    com.sun.net.httpserver.HttpContext getHttpContext();

    InputStream getRequestBody();

    OutputStream getResponseBody();

    void sendResponseHeaders(int var1, long var2) throws IOException;

    InetSocketAddress getRemoteAddress();

    int getResponseCode();

    String getProtocol();

    Object getAttribute(String var1);

    void setAttribute(String var1, Object var2);

    void setStreams(InputStream var1, OutputStream var2);

    HttpPrincipal getPrincipal();
}
