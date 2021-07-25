package de.lystx.hytoracloud.driver.commons.http.client;

import de.lystx.hytoracloud.driver.commons.http.utils.HttpCallback;
import de.lystx.hytoracloud.driver.commons.http.requests.HttpRequest;
import de.lystx.hytoracloud.driver.commons.http.utils.HttpRequestType;
import de.lystx.hytoracloud.driver.commons.http.utils.HttpResponse;
import de.lystx.hytoracloud.driver.commons.http.withbody.HttpDeleteWithBody;
import de.lystx.hytoracloud.driver.commons.http.withbody.HttpOptionsWithBody;
import de.lystx.hytoracloud.driver.commons.http.withbody.HttpPatchWithBody;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.entity.NByteArrayEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;


@Getter @AllArgsConstructor
public class ClientRequest {

    /**
     * The request type
     */
    public final HttpRequestType httpRequestType;

    /**
     * The requested url
     */
    public final String url;

    /**
     * The http client
     */
    private final CloseableHttpClient syncClient;

    /**
     * The default headers
     */
    private final Map<String, Object> defaultHeaders;


    /**
     * Loads the {@link HttpRequest} from this request
     *
     * @param request the request
     * @param responseClass the class
     * @param <T> the generic
     * @return response
     */
    public <T> HttpResponse<T> loadResponse(HttpRequest request, Class<T> responseClass) {
        if (syncClient == null) {
            throw new RuntimeException("Sync client not configured");
        }
        HttpRequestBase requestObj = prepareRequest(request);
        org.apache.http.HttpResponse response;
        try {
            response = syncClient.execute(requestObj);
            return HttpResponse.create(requestObj, response, responseClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares the current request
     *
     * @param request the request
     * @return base object
     */
    private HttpRequestBase prepareRequest(HttpRequest request) {

        if (defaultHeaders != null) {
            for (Map.Entry<String, Object> entry : defaultHeaders.entrySet()) {
                //Do not set content-type for multipart and urlencoded
                if (!entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_TYPE) || request.getBody() == null || !request.getBody().isImplicitContentType()) {
                    request.header(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
        }

        if (!request.getHeaders().containsKey(HttpHeaders.USER_AGENT)) {
            request.header(HttpHeaders.USER_AGENT, Utils.USER_AGENT);
        }
        if (!request.getHeaders().containsKey(HttpHeaders.ACCEPT_ENCODING)) {
            request.header(HttpHeaders.ACCEPT_ENCODING, Utils.GZIP);
        }

        String urlToRequest;
        try {
            URL reqUrl = new URL(request.getUrl());
            URI uri = new URI(reqUrl.getProtocol(), reqUrl.getUserInfo(), reqUrl.getHost(), reqUrl.getPort(), URLDecoder.decode(reqUrl.getPath(), Utils.UTF_8), "", reqUrl.getRef());
            urlToRequest = uri.toURL().toString();
            if (reqUrl.getQuery() != null && !reqUrl.getQuery().trim().equals("")) {
                if (!urlToRequest.endsWith(Utils.QUESTION_MARK)) {
                    urlToRequest += Utils.QUESTION_MARK;
                }
                urlToRequest += reqUrl.getQuery();
            } else if (urlToRequest.endsWith(Utils.QUESTION_MARK)) {
                urlToRequest = urlToRequest.substring(0, urlToRequest.length() - 1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpRequestBase reqObj;
        switch (request.getHttpRequestType()) {
            case GET:
                reqObj = new HttpGet(urlToRequest);
                break;
            case POST:
                reqObj = new HttpPost(urlToRequest);
                break;
            case PUT:
                reqObj = new HttpPut(urlToRequest);
                break;
            case DELETE:
                reqObj = new HttpDeleteWithBody(urlToRequest);
                break;
            case PATCH:
                reqObj = new HttpPatchWithBody(urlToRequest);
                break;
            case OPTIONS:
                reqObj = new HttpOptionsWithBody(urlToRequest);
                break;
            case HEAD:
                reqObj = new HttpHead(urlToRequest);
                break;
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + request.getHttpRequestType());
        }

        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            List<String> values = entry.getValue();
            if (values != null) {
                for (String value : values) {
                    reqObj.addHeader(entry.getKey(), value);
                }
            }
        }

        // Set body
        if (request.getHttpRequestType() != HttpRequestType.GET && request.getHttpRequestType() != HttpRequestType.HEAD) {
            if (request.getBody() != null) {
                HttpEntity entity = request.getBody().getEntity();
                ((HttpEntityEnclosingRequestBase) reqObj).setEntity(entity);
            }
        }

        return reqObj;
    }
}
