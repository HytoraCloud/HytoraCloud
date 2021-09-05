package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component.NettyComponent;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component.SimpleComponent;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future.NettyFuture;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future.SimpleFuture;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.handler.IRequestHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling.IChannelHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.PacketChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.ConnectionType;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import lombok.Getter;

import java.util.*;

@Getter
public class DefaultRequestManager implements IRequestManager {

    /**
     * All stored futures with their ids
     */
    private final Map<String, NettyFuture<?>> futures;

    /**
     * Request handlers for api request-response
     */
    private final List<IRequestHandler<?>> requestHandlers;

    /**
     * The parent connection
     */
    private final INetworkConnection networkConnection;

    public DefaultRequestManager(INetworkConnection networkConnection) {
        this.networkConnection = networkConnection;
        this.futures = new HashMap<>();
        this.requestHandlers = new LinkedList<>();

        this.networkConnection.registerChannelHandler("cloud::api::request", new IChannelHandler() {
            @Override
            public void handle(PacketChannelMessage packet, String json, IChannelMessage message) {
                NettyComponent<?> request = message.getDocument().get("request", SimpleComponent.class);

                if (networkConnection.getType() == ConnectionType.CLOUD_INSTANCE) {
                    if (request.getTarget() == null || request.getTarget().getName().equalsIgnoreCase("CLOUD")) {
                        for (IRequestHandler requestHandler : requestHandlers) {
                            requestHandler.handle(request);
                        }
                    } else {
                        NettyFuture<?> comply = request.queryRequest(networkConnection);
                        ((SimpleComponent<?>)request.createResponse(request.typeClass())).data(comply.pullValue()).post(networkConnection);
                    }
                    return;
                }
                if (networkConnection.getType() == ConnectionType.CLOUD_RECEIVER) {
                    if (request.getTarget() == null || request.getTarget().getName().equalsIgnoreCase("RECEIVER")) {
                        for (IRequestHandler requestHandler : requestHandlers) {
                            requestHandler.handle(request);
                        }
                    }
                    return;
                }
                if (networkConnection.getType() == ConnectionType.CLOUD_BRIDGE) {
                    if (request.getTarget() == null || request.getTarget().getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName()) || request.getTarget().getName().equalsIgnoreCase("PROXY") && CloudDriver.getInstance().getServiceManager().getThisService().getGroup().getEnvironment() == ServerEnvironment.PROXY || request.getTarget().getName().equalsIgnoreCase("BUKKIT") && CloudDriver.getInstance().getServiceManager().getThisService().getGroup().getEnvironment() == ServerEnvironment.SPIGOT) {
                        for (IRequestHandler requestHandler : requestHandlers) {
                            requestHandler.handle(request);
                        }
                    }
                }

            }
        });

        this.networkConnection.registerChannelHandler("cloud::api::response", new IChannelHandler() {

            @Override
            public void handle(PacketChannelMessage packet, String json, IChannelMessage message) {
                try {
                    JsonObject<?> document = message.getDocument().getObject("response");
                    SimpleComponent<?> response = new SimpleComponent<>();

                    response.setResponse(true);
                    response.id(document.getString("id"));
                    response.typeClass(Class.forName(document.getString("typeClass")));
                    response.success(document.getBoolean("success"));
                    response.setCompletionTimeMillis(packet.getDif());

                    Class<?> aClass = response.typeClass();

                    if (aClass.equals(Boolean.class) || aClass.equals(boolean.class)) {
                        response.data(document.getBoolean("data"));
                    } else if (aClass.equals(Integer.class) || aClass.equals(int.class)) {
                        response.data(document.getInteger("data"));
                    } else if (aClass.equals(Long.class) || aClass.equals(long.class)) {
                        response.data(document.getLong("data"));
                    } else if (aClass.equals(Short.class) || aClass.equals(short.class)) {
                        response.data(document.getShort("data"));
                    } else if (aClass.equals(Byte.class) || aClass.equals(byte.class)) {
                        response.data(document.getByte("data"));
                    } else if (aClass.equals(Double.class) || aClass.equals(double.class)) {
                        response.data(document.getDouble("data"));
                    } else if (aClass.equals(Float.class) || aClass.equals(float.class)) {
                        response.data(document.getFloat("data"));
                    } else if (aClass.equals(UUID.class)) {
                        response.data(UUID.fromString(document.getString("data")));
                    } else if (aClass.equals(String.class)) {
                        response.data(document.getString("data"));
                    } else if (Enum.class.isAssignableFrom(aClass)) {
                        response.data(Utils.getEnumByName(aClass, document.getString("data")));
                    } else {
                        response.data(document.get("data", aClass));
                    }

                    String exceptionClass = document.getString("exceptionClass");
                    if (!exceptionClass.equalsIgnoreCase("None")) {
                        if (document.getElement("exception").isJsonNull()) {
                            response.exception(null);
                        } else {
                            Class<?> exClass = Class.forName(exceptionClass);
                            Object exception = document.get("exception", exClass);
                            response.exception((Throwable) exception);
                        }
                    }

                    SimpleFuture<?> future = (SimpleFuture<?>) retrieveFuture(response.getId());
                    if (future == null) {
                        return;
                    }
                    NettyComponent<?> request = future.getRequest();
                    request.typeClass(response.typeClass());
                    ((SimpleComponent<?>)request).setCompletionTimeMillis(packet.getDif());
                    future.setCompletionTimeMillis(System.currentTimeMillis() - future.getCompletionTimeMillis());
                    future.setRequest(request);
                    future.completeFuture(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void registerRequestHandler(IRequestHandler<?> handler) {
        this.requestHandlers.add(handler);
    }

    @Override
    public void unregisterRequestHandler(IRequestHandler<?> handler) {
        this.requestHandlers.remove(handler);
    }

    /**
     * Adds a {@link NettyFuture} to the cache with a given id
     *
     * @param id the id of the request
     * @param future the future
     */
    public void addRequest(String id, NettyFuture<?> future) {
        futures.put(id, future);
    }

    /**
     * Gets an {@link NettyFuture} from cache
     * and then automatically removes it from cache
     *
     * @param id the id
     * @return future or null
     */
    public NettyFuture<?> retrieveFuture(String id) {
        NettyFuture<?> future = futures.get(id);
        futures.remove(id);
        return future;
    }

    @Override
    public IChannelMessage toMessage(NettyComponent<?> component) {

        String channel = component.isResponse() ? "cloud::api::response" : "cloud::api::request";
        String key = component.isResponse() ? "response" : "request";

        if (component.isResponse()) {
            JsonObject<JsonDocument> jsonObject = JsonObject.gson();

            jsonObject.append(key,
                    JsonObject.gson()
                            .append("response", component.isResponse())
                            .append("target", component.getTarget() == null ? "ALL" : component.getTarget().getName())
                            .append("document", component.getDocument().toString())
                            .append("id", component.getId())
                            .append("success", component.isSuccess())
                            .append("data", component.getData())
                            .append("typeClass", component.typeClass().getName())
                            .append("exception", component.getException() == null ? "null" : component.getException())
                            .append("exceptionClass", component.getException() == null ? "None" : component.getException().getClass().getName())
                            .append("key", component.getKey())

            );

            return IChannelMessage.builder().channel(channel).document(jsonObject).build();
        } else {

            Identifiable target;
            if (component.getTarget() == null || component.getTarget().getName().equalsIgnoreCase("ALL") || component.getTarget().getName().equalsIgnoreCase("BUKKIT") || component.getTarget().getName().equalsIgnoreCase("PROXY")) {
                target = Identifiable.ALL;
            } else {
                target = component.getTarget();
            }
            return IChannelMessage.builder().channel(channel).receiver(target).document(new JsonDocument().append(key, component)).build();
        }
    }

}
