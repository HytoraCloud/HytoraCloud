package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component;

import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.IRequestManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future.NettyFuture;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future.SimpleFuture;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.wrapped.IdentifiableObject;
import lombok.*;

import java.util.concurrent.ThreadLocalRandom;

@Getter @AllArgsConstructor @NoArgsConstructor @Setter
public class SimpleComponent<T> implements NettyComponent<T> {

    /**
     * The document
     */
    private String document;

    /**
     * The key
     */
    private String key;

    /**
     * If this component is a reply
     */
    private boolean response;

    /**
     * The id of this response
     */
    private String id = String.valueOf(ThreadLocalRandom.current().nextLong());

    /**
     * If success
     */
    private boolean success;

    /**
     * The target
     */
    private IdentifiableObject target;

    /**
     * The error
     */
    private Throwable exception;

    /**
     * The data
     */
    private T data;

    /**
     * The type class
     */
    private String typeClass;

    /**
     * The time the future took
     */
    private long completionTimeMillis;

    public NettyComponent<T> data(Object data) {
        this.typeClass(data.getClass());
        this.data = (T) data;
        this.success = true;
        return this;
    }

    @Override
    public NettyComponent<T> value(T t) {
        return this.data(t);
    }

    @Override @SneakyThrows
    public Class<T> typeClass() {
        return (Class<T>) Class.forName(typeClass);
    }

    @Override
    public NettyComponent<T> typeClass(Class<?> typeClass) {
        this.typeClass = typeClass.getName();
        return this;
    }

    @Override
    public NettyComponent<T> key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public <V> NettyComponent<V> createResponse(Class<V> vClass) {
        SimpleComponent<V> response = new SimpleComponent<>();
        response.setResponse(true);
        response.id(this.id);
        response.typeClass(vClass);
        return response;
    }

    @Override
    public NettyFuture<T> queryRequest(INetworkConnection networkConnection) {
        NettyFuture<T> query = new SimpleFuture<>(this);
        IRequestManager requestManager = networkConnection.getRequestManager();

        ((SimpleFuture<T>)query).setCompletionTimeMillis(System.currentTimeMillis());

        requestManager.addRequest(this.id, query);
        post(networkConnection);
        return query;
    }

    @Override
    public NettyComponent<T> document(JsonObject<?> document) {
        this.document = document.toString();
        return this;
    }

    public JsonObject<?> getDocument() {
        return JsonObject.gson(document);
    }

    @Override
    public NettyComponent<T> id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public NettyComponent<T> exception(Throwable throwable) {
        this.exception = throwable;
        this.success = false;
        return this;
    }

    @Override
    public NettyComponent<T> success(boolean success) {
        this.success = success;
        return this;
    }

    @Override
    public void post(INetworkConnection connection) {
        IRequestManager requestManager = connection.getRequestManager();
        IChannelMessage message = requestManager.toMessage(this);
        connection.sendChannelMessage(message);
    }

}
