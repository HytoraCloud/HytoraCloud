package de.lystx.hytoracloud.driver.commons.http.client;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ClientBuilder {

    /**
     * The credentials from apache
     */
    private CredentialsProvider credentialsProvider;

    /**
     * The current base url
     */
    private String baseUrl = "";

    /**
     * The url transformer
     */
    private final Function<String, String> urlTransformer;

    /**
     * All headers
     */
    private final Map<String, Object> defaultHeaders;

    /**
     * The config builder
     */
    private final RequestConfig.Builder configBuilder;

    /**
     * The cookie storage
     */
    private final CookieStore cookieStore;

    public ClientBuilder() {
        this.urlTransformer = url -> url;
        this.defaultHeaders = new HashMap<>();
        this.configBuilder = RequestConfig.custom();
        this.cookieStore = new BasicCookieStore();
    }

    /**
     * Builds the current client
     * @return built client
     */
    public HttpRestClient build() {
        try {

            RequestConfig clientConfig = configBuilder.build();
            int maxTotal = 20;
            int maxRoute = 2;

            PoolingHttpClientConnectionManager syncConnectionManager = new PoolingHttpClientConnectionManager();

            syncConnectionManager.setMaxTotal(maxTotal);
            syncConnectionManager.setDefaultMaxPerRoute(maxRoute);

            HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                    .setDefaultRequestConfig(clientConfig)
                    .setDefaultCookieStore(cookieStore)
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setConnectionManager(syncConnectionManager);


            DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
            PoolingNHttpClientConnectionManager asyncConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
            asyncConnectionManager.setMaxTotal(maxTotal);
            asyncConnectionManager.setDefaultMaxPerRoute(maxRoute);
            HttpRestClient httpRestClient = new HttpRestClient(baseUrl, defaultHeaders, urlTransformer, syncConnectionManager, clientBuilder.build(), cookieStore);
            ClientContainer.CLIENTS.put(httpRestClient.getId(), httpRestClient);
            return httpRestClient;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the base url
     *
     * @param baseUrl the url
     * @return current builder
     */
    public ClientBuilder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Sets the default header
     *
     * @return current builder
     */
    public ClientBuilder defaultHeader(String key, String value) {
        this.defaultHeaders.put(key, value);
        return this;
    }

    /**
     * Toggles following redirect
     *
     * @return current builder
     */
    public ClientBuilder followRedirect(boolean followRedirect) {
        configBuilder.setRedirectsEnabled(followRedirect);
        return this;
    }

    /**
     * Sets the cookie spec
     *
     * @return current builder
     */
    public ClientBuilder cookieSpec(String cookieSpec) {
        configBuilder.setCookieSpec(cookieSpec);
        return this;
    }

    /**
     * Sets the proxy
     *
     * @return current builder
     */
    public ClientBuilder proxy(HttpHost proxy) {
        configBuilder.setProxy(proxy);
        return this;
    }

    public ClientBuilder proxy(HttpHost proxy, Credentials credentials) {
        this.credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(proxy.getHostName(), proxy.getPort()), credentials);
        return this.proxy(proxy);
    }

    /**
     * Set the connection timeout and socket timeout
     *
     * @param connectionTimeout The timeout until a connection with the server is established (in milliseconds). Default is 10000. Set to zero to disable the timeout.
     * @param readTimeout       The timeout to receive data (in milliseconds). Default is 60000. Set to zero to disable the timeout.
     */
    public ClientBuilder timeout(int connectionTimeout, int readTimeout) {
        configBuilder.setSocketTimeout(readTimeout).setConnectTimeout(connectionTimeout);
        return this;
    }

}
