
package de.lystx.hytoracloud.driver.connection.http.client;

import de.lystx.hytoracloud.driver.connection.http.utils.HttpRequestType;
import de.lystx.hytoracloud.driver.connection.http.requests.HttpRequest;
import de.lystx.hytoracloud.driver.connection.http.requests.HttpBodyRequest;
import lombok.Getter;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static de.lystx.hytoracloud.driver.utils.other.Utils.PATH_SEPARATOR;
@Getter
public class HttpRestClient implements Closeable {

    private static final int IDLE_CONNECTION_TIMEOUT = 30;

    public final String id;

    private final Function<String, String> urlTransformer;

    private final String baseUrl;
    private final PoolingNHttpClientConnectionManager asyncConnectionManager;
    private final PoolingHttpClientConnectionManager syncConnectionManager;

    private final Map<String, Object> defaultHeaders;

    private final CloseableHttpAsyncClient asyncClient;
    private final CloseableHttpClient syncClient;
    private final CookieStore cookieStore;

    public HttpRestClient(String baseUrl, Map<String, Object> defaultHeaders, Function<String, String> urlTransformer, PoolingHttpClientConnectionManager syncConnectionManager, CloseableHttpClient syncClient, CookieStore cookieStore) {
        this.baseUrl = baseUrl;
        this.urlTransformer = urlTransformer;
        this.asyncConnectionManager = null;
        this.syncConnectionManager = syncConnectionManager;
        this.asyncClient = null;
        this.syncClient = syncClient;
        this.cookieStore = cookieStore;
        this.defaultHeaders = defaultHeaders;
        this.id = newUUID();
    }

    private HttpRestClient(HttpClientBuilder clientBuilder, HttpAsyncClientBuilder asyncClientBuilder) {
        this.id = newUUID();
        this.baseUrl = "";
        this.urlTransformer = url -> url;
        this.cookieStore = new BasicCookieStore();
        this.defaultHeaders = new HashMap<>();

        if (clientBuilder != null) {
            this.syncConnectionManager = new PoolingHttpClientConnectionManager();
            clientBuilder.setConnectionManager(syncConnectionManager);
            clientBuilder.setDefaultCookieStore(cookieStore);
            this.syncClient = clientBuilder.build();
        } else {
            this.syncClient = null;
            this.syncConnectionManager = null;
        }

        if (asyncClientBuilder != null) {
            try {
                PoolingNHttpClientConnectionManager asyncConnManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
                asyncClientBuilder.setConnectionManager(asyncConnManager);
                asyncClientBuilder.setDefaultCookieStore(cookieStore);
                this.asyncClient = asyncClientBuilder.build();
                this.asyncConnectionManager = asyncConnManager;
            } catch (IOReactorException e) {
                throw new IllegalStateException("Failed to Async IO reactor", e);

            }
        } else {
            this.asyncClient = null;
            this.asyncConnectionManager = null;
        }
    }

    public static ClientBuilder builder() {
        return new ClientBuilder();
    }

    public static HttpRestClient with(HttpClientBuilder clientBuilder) {
        return with(clientBuilder, null);
    }

    public static HttpRestClient with(HttpClientBuilder clientBuilder, HttpAsyncClientBuilder asyncClientBuilder) {
        return new HttpRestClient(clientBuilder, asyncClientBuilder);
    }

    public HttpRequest get(String... url) {
        return new HttpRequest(new ClientRequest(HttpRequestType.GET, resolveUrl(url), syncClient, defaultHeaders));
    }

    public HttpRequest head(String... url) {
        return new HttpRequest(new ClientRequest(HttpRequestType.HEAD, resolveUrl(url), syncClient, defaultHeaders));
    }

    public HttpBodyRequest options(String... url) {
        return new HttpBodyRequest(new ClientRequest(HttpRequestType.OPTIONS, resolveUrl(url), syncClient, defaultHeaders));
    }

    public HttpBodyRequest post(String... url) {
        return new HttpBodyRequest(new ClientRequest(HttpRequestType.POST, resolveUrl(url), syncClient, defaultHeaders));
    }

    public HttpBodyRequest delete(String... url) {
        return new HttpBodyRequest(new ClientRequest(HttpRequestType.DELETE, resolveUrl(url), syncClient, defaultHeaders));
    }

    public HttpBodyRequest patch(String... url) {
        return new HttpBodyRequest(new ClientRequest(HttpRequestType.PATCH, resolveUrl(url), syncClient, defaultHeaders));
    }

    public HttpBodyRequest put(String... url) {
        return new HttpBodyRequest(new ClientRequest(HttpRequestType.PUT, resolveUrl(url), syncClient, defaultHeaders));
    }

    public CookieStore cookieStore() {
        return cookieStore;
    }

    String resolveUrl(String... paths) {
        StringJoiner pathJoiner = new StringJoiner(PATH_SEPARATOR);
        for (String path : paths) {
            if(path != null && !path.trim().isEmpty()) {
                path = path.startsWith(PATH_SEPARATOR) ? path.substring(1) : path;
                path = path.endsWith(PATH_SEPARATOR) ? path.substring(0, path.length() - 1) : path;
                pathJoiner.add(path);
            }
        }

        if(baseUrl == null || baseUrl.trim().isEmpty()) {
            return pathJoiner.toString();
        }
        String base = urlTransformer.apply(baseUrl);
        base = !base.endsWith(PATH_SEPARATOR) ? base + PATH_SEPARATOR : base;
        return base + pathJoiner.toString();
    }

    public void closeIdleConnections() {
        if (asyncConnectionManager != null) {
            asyncConnectionManager.closeExpiredConnections();
            asyncConnectionManager.closeIdleConnections(IDLE_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        }
        if (syncConnectionManager != null) {
            syncConnectionManager.closeExpiredConnections();
            syncConnectionManager.closeIdleConnections(IDLE_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    /**
     * Close the asynchronous client and its event loop. Use this method to close all the threads and allow an application to exit.
     */
    @Override
    public void close() {
        try {
            if (syncClient != null) {
                syncClient.close();
                syncConnectionManager.close();
            }

            if (asyncClient != null && asyncClient.isRunning()) {
                asyncClient.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        ClientContainer.CLIENTS.remove(this.id);
    }

    private String newUUID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
