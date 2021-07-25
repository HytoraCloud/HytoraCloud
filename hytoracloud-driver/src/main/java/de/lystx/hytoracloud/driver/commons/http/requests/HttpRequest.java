
package de.lystx.hytoracloud.driver.commons.http.requests;

import de.lystx.hytoracloud.driver.commons.http.client.ClientRequest;
import de.lystx.hytoracloud.driver.commons.http.utils.HttpRequestType;
import de.lystx.hytoracloud.driver.commons.http.mapper.MimeMappings;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import org.apache.http.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

@Getter
public class HttpRequest extends BaseRequest {

    private static final MimeMappings MIME_MAPPINGS = MimeMappings.builder().build();

    /**
     * The url of this request
     */
    private String url;

    /**
     * The body of this request
     */
    protected HttpBody body;

    /**
     * The headers for this request
     */
    private final Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

    /**
     * The Http method for this request
     */
    private final HttpRequestType httpRequestType;

    public HttpRequest(ClientRequest request) {
        super(null, request);
        this.url = request.getUrl();
        this.httpRequestType = request.getHttpRequestType();
        this.httpRequest = this;
    }

    public HttpRequest basicAuth(String username, String password) {
        header(HttpHeaders.AUTHORIZATION, "Basic " + Utils.encodeString(username + ":" + password));
        return this;
    }

    /**
     * Adds a header for this request
     *
     * @param name the name of the header
     * @param obj the value
     * @return current request
     */
    public HttpRequest header(String name, Object obj) {
        String value = obj.toString();
        List<String> list = this.headers.get(name.trim());
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(value);
        this.headers.put(name.trim(), list);
        return this;
    }

    /**
     * Sets the content type of this request
     *
     * @param contentType the type
     * @return current request
     */
    public HttpRequest contentType(RequestType contentType) {
        if (contentType == null || contentType.toString().isEmpty()) {
            return this;
        }
        if (!contentType.toString().contains("/")) {
            String mimeType = MIME_MAPPINGS.getMimeType(contentType.toString());
            if (mimeType != null) {
                return this.header(HttpHeaders.CONTENT_TYPE, mimeType);
            }
        }

        this.header(HttpHeaders.CONTENT_TYPE, contentType);
        return this;
    }

    /**
     * Queries a string with a given value
     *
     * @param name the name
     * @param value the value
     * @return current request
     */
    public HttpRequest queryString(String name, Object value) {
        StringBuilder queryString = new StringBuilder();
        if (this.url.contains(Utils.QUESTION_MARK)) {
            queryString.append(Utils.AMPERSAND);
        } else {
            queryString.append(Utils.QUESTION_MARK);
        }
        try {
            queryString
                    .append(URLEncoder.encode(name, Utils.UTF_8))
                    .append(Utils.EQUALS)
                    .append(URLEncoder.encode(value == null ? "" : value.toString(), Utils.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.url += queryString.toString();
        return this;
    }

    /**
     * Queries all strings (request-properties) and load them
     * from a given parameter-map
     *
     * @param parameters the parameters
     * @return current request
     */
    public HttpRequest queryString(Map<String, Object> parameters) {
        if (parameters != null) {
            for (Entry<String, Object> param : parameters.entrySet()) {
                if (param.getValue() instanceof String || param.getValue() instanceof Number || param.getValue() instanceof Boolean) {
                    queryString(param.getKey(), param.getValue());
                } else {
                    throw new RuntimeException("Parameter \"" + param.getKey() + "\" can't be sent with a GET request because of type: " + param.getValue().getClass().getName());
                }
            }
        }
        return this;
    }

    /**
     * Gets the current headers of this request
     * If no headers are set it will return an empty map
     *
     * @return map of headers
     */
    public Map<String, List<String>> getHeaders() {
        return this.headers == null ? new HashMap<>() : headers;
    }

    /**
     * Gets the content type of this request
     *
     * @return type
     */
    public RequestType getContentType() {
        List<String> types = this.headers.get(HttpHeaders.CONTENT_TYPE);
        if (types == null || types.isEmpty()) {
            throw new IllegalStateException("Content-Type not specified");
        }
        //Content-type is always a single value
        String type = types.get(0);
        return RequestType.valueOf(type);
    }

}
