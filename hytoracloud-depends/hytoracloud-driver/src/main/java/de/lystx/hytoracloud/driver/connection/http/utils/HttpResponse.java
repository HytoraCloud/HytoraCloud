
package de.lystx.hytoracloud.driver.connection.http.utils;

import de.lystx.hytoracloud.driver.connection.http.mapper.ObjectMapper;
import de.lystx.hytoracloud.driver.connection.http.mapper.ObjectMappers;
import de.lystx.hytoracloud.driver.connection.http.requests.RequestType;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import lombok.Getter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Getter
public class HttpResponse<T> implements Closeable {

    /**
     * The status response code
     */
    private final int status;

    /**
     * The status text
     */
    private final String message;

    /**
     * The headers
     */
    private final HttpHeaders httpHeaders;

    /**
     * The body as input stream
     */
    protected final InputStream rawBody;

    /**
     * The class of the object
     */
    private final Class<T> responseClass;

    /**
     * all cached bytes
     */
    private byte[] cached;

    public HttpResponse(org.apache.http.HttpResponse response, Class<T> responseClass) {
        this.responseClass = responseClass;

        this.rawBody = this.toStream(response);
        this.httpHeaders = this.loadHeaders(response);

        this.status = response.getStatusLine() != null ? response.getStatusLine().getStatusCode() : -1;
        this.message = response.getStatusLine() != null ? response.getStatusLine().getReasonPhrase() : "NOT_SET";
    }

    /**
     * Checks if the response was successfully handled
     *
     * @return boolean
     */
    public boolean isSuccessful() {
        return status >= 200 && status < 300 || status == 304;
    }

    /**
     * Gets body as generic object
     *
     * @return the object
     */
    public T getBody() {
        if (InputStream.class.equals(responseClass)) {
            if (cached != null) {
                return (T) new ByteArrayInputStream(cached);
            }
            return (T) this.rawBody;
        }

        String bodyString = asString();

        if (JsonNode.class.equals(responseClass)) {
            return (T) new JsonNode(bodyString);
        } else if (String.class.equals(responseClass)) {
            return (T) bodyString;
        } else {
            return getObjectMapper().read(bodyString, responseClass);
        }
    }

    /**
     * Gets the body as a given object
     *
     * @param type the class
     * @param <E> the generic
     * @return parsed body object
     */
    public <E> E getBody(Class<E> type) {
        return getObjectMapper().read(asString(), type);
    }

    /**
     * Returns this response as String
     *
     * @return string response
     */
    public String asString() {
        try {
            if (this.rawBody == null && cached == null) {
                return null;
            }

            if(cached == null) {
                cached = Utils.readBytes(rawBody);
                rawBody.close();
            }

            String charset = Utils.UTF_8;

            String contentType = httpHeaders.getFirst(org.apache.http.HttpHeaders.CONTENT_TYPE);
            if (contentType != null) {
                String responseCharset = Utils.getCharsetFromContentType(contentType);
                if (responseCharset != null && !responseCharset.trim().equals("")) {
                    charset = responseCharset;
                }
            }
            return new String(cached, charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the {@link ObjectMapper} for this response
     *
     * @return mapper
     */
    private ObjectMapper getObjectMapper() {
        String contentType = httpHeaders.getFirst(org.apache.http.HttpHeaders.CONTENT_TYPE);
        if (contentType == null) {
            throw new RuntimeException("Response Content-Type header not found");
        }
        return ObjectMappers.getMapper(RequestType.valueOf(contentType));
    }

    /**
     * Closes this response
     */
    @Override
    public void close() {
        if (rawBody == null) {
            return;
        }

        try {
            rawBody.close();
        } catch (IOException ignore) {
            //Ignoring exception
        }
    }

    /**
     * Loads {@link HttpHeaders} from response
     *
     * @param response the response
     * @return headers object
     */
    private HttpHeaders loadHeaders(org.apache.http.HttpResponse response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Header[] allHeaders = response.getAllHeaders();
        for (Header header : allHeaders) {
            String headerName = header.getName();
            List<String> list = httpHeaders.get(headerName);
            if (list == null)
                list = new ArrayList<>();
            list.add(header.getValue());
            httpHeaders.put(headerName, list);
        }
        return httpHeaders;
    }

    /**
     * Transforms {@link HttpEntity} to {@link InputStream}
     *
     * @param entity the entity
     * @return stream
     * @throws IOException if something goes wrong while using {@link GZIPInputStream}
     */
    private InputStream entityToStream(HttpEntity entity) throws IOException {
        if (Utils.isGzipped(entity.getContentEncoding()) && entity.getContentLength() > 0) {
            return new GZIPInputStream(entity.getContent());
        }
        return entity.getContent();
    }

    /**
     * Loads an {@link InputStream} from an {@link org.apache.http.HttpResponse}
     *
     * @param response the response
     * @return stream or null if error
     */
    protected InputStream toStream(org.apache.http.HttpResponse response) {
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity == null) {
            return null;
        }

        try {
            InputStream entity = entityToStream(httpEntity);
            return new ByteArrayInputStream(Utils.readBytes(entity));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            EntityUtils.consumeQuietly(httpEntity);
        }
    }

    /**
     * Creates a {@link HttpResponse} from request base and class object
     *
     * @param request the request
     * @param response the apache response
     * @param responseClass the class
     * @param <T> the generic
     * @return response
     */
    public static <T> HttpResponse<T> create(HttpRequestBase request, org.apache.http.HttpResponse response, Class<T> responseClass) {
        if (responseClass == InputStream.class) {
            return new HttpStreamResponse<>(response, responseClass, request);
        }
        return new HttpResponse<>(response, responseClass);
    }


}
