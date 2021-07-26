package de.lystx.hytoracloud.driver.commons.http.listener;

import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IResponse extends Serializable {

    IContext context();

    IResponse statusCode(int code);

    IResponse content(JsonDocument document);

    JsonDocument document();

    int statusCode();

    Map<String, List<String>> headers();

    IResponse header(String name, String value);


    IResponse body(byte[] bytes);

    IResponse body(String text);

}
