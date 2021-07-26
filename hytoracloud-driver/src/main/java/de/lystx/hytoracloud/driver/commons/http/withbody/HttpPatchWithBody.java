
package de.lystx.hytoracloud.driver.commons.http.withbody;

import de.lystx.hytoracloud.driver.commons.http.utils.HttpRequestType;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpPatchWithBody extends HttpEntityEnclosingRequestBase {

    public HttpPatchWithBody(final String uri) {
        setURI(URI.create(uri));
    }

    public String getMethod() {
        return HttpRequestType.PATCH.name();
    }
}