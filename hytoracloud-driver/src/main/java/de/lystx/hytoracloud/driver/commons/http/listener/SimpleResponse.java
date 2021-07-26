package de.lystx.hytoracloud.driver.commons.http.listener;

import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter @RequiredArgsConstructor
public class SimpleResponse implements IResponse {

    private static final long serialVersionUID = -9152892963327812694L;
    private final IContext context;
    private JsonDocument document = new JsonDocument();

    private int code;

    private final Map<String, List<String>> headers = new CloudMap<>();

    @Override
    public IContext context() {
        return context;
    }

    @Override
    public IResponse statusCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public IResponse content(JsonDocument document) {
        this.document = document;
        return this;
    }

    @Override
    public JsonDocument document() {
        return document;
    }

    @Override
    public int statusCode() {
        return this.code;
    }

    @Override
    public Map<String, List<String>> headers() {
        return headers;
    }

    @Override
    public IResponse header(String name, String value) {
        List<String> orDefault = this.headers.getOrDefault(name, new LinkedList<>());
        orDefault.add(value);
        this.headers.put(name, orDefault);
        return this;
    }

    @Override
    public IResponse body(byte[] bytes) {
        return null;
    }

    @Override
    public IResponse body(String text) {
        return null;
    }


}
