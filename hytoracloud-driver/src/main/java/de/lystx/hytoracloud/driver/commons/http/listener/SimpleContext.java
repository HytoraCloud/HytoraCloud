package de.lystx.hytoracloud.driver.commons.http.listener;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

@Getter @AllArgsConstructor
public class SimpleContext implements IContext {

    private static final long serialVersionUID = -8189096333743814756L;
    private final HttpExchange exchange;

    @Override
    public IResponse createResponse() {
        return new SimpleResponse(this);
    }

    @Override
    public void close() {
        this.exchange.close();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.exchange.getLocalAddress();
    }

    @Override
    public Headers getRequestHeaders() {
        return this.exchange.getRequestHeaders();
    }

    @Override
    public Headers getResponseHeaders() {
        return this.exchange.getResponseHeaders();
    }

    @Override
    public URI getRequestURI() {
        return this.exchange.getRequestURI();
    }

    @Override
    public String getRequestMethod() {
        return this.exchange.getRequestMethod();
    }

    @Override
    public com.sun.net.httpserver.HttpContext getHttpContext() {
        return this.exchange.getHttpContext();
    }

    @Override
    public InputStream getRequestBody() {
        return this.exchange.getRequestBody();
    }

    @Override
    public OutputStream getResponseBody() {
        return this.exchange.getResponseBody();
    }

    @Override
    public void sendResponseHeaders(int var1, long var2) throws IOException {
        this.exchange.sendResponseHeaders(var1, var2);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.exchange.getRemoteAddress();
    }

    @Override
    public int getResponseCode() {
        return this.exchange.getResponseCode();
    }

    @Override
    public String getProtocol() {
        return this.exchange.getProtocol();
    }

    @Override
    public Object getAttribute(String var1) {
        return this.exchange.getAttribute(var1);
    }

    @Override
    public void setAttribute(String var1, Object var2) {
        this.exchange.setAttribute(var1, var2);
    }

    @Override
    public void setStreams(InputStream var1, OutputStream var2) {
        this.exchange.setStreams(var1, var2);
    }

    @Override
    public HttpPrincipal getPrincipal() {
        return this.exchange.getPrincipal();
    }
}
