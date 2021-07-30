package de.lystx.hytoracloud.driver.commons.requests;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.requests.base.IQuery;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverResponse;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverResponseObject;
import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.requests.base.SimpleQuery;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequestObject;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class RequestManager {

    /**
     * All stored futures with their ids
     */
    private final Map<String, IQuery<?>> futures;

    /**
     * Request handlers for api request-response
     */
    private final List<Consumer<DriverRequest<?>>> requestHandlers;

    public RequestManager() {
        this.futures = new HashMap<>();
        this.requestHandlers = new LinkedList<>();

        CloudDriver.getInstance().getMessageManager().registerChannel("API_REQUESTS", new Consumer<IChannelMessage>() {
            @Override
            public void accept(IChannelMessage channelMessage) {

                DriverRequest<?> request = channelMessage.getDocument().get("request", DriverRequestObject.class);

                if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
                    if (request.getTarget() == null || request.getTarget().equalsIgnoreCase("CLOUD")) {
                        for (Consumer<DriverRequest<?>> requestHandler : requestHandlers) {
                            requestHandler.accept(request);
                        }
                    } else {
                        IQuery<?> comply = request.execute();
                        request.createResponse(request.typeClass()).data(comply.pullValue()).send();
                    }
                    return;
                }
                if (CloudDriver.getInstance().getDriverType() == CloudType.RECEIVER) {
                    if (request.getTarget() == null || request.getTarget().equalsIgnoreCase("RECEIVER")) {
                        for (Consumer<DriverRequest<?>> requestHandler : requestHandlers) {
                            requestHandler.accept(request);
                        }
                    }
                    return;
                }
                if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
                    if (request.getTarget() == null || request.getTarget().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getCurrentService().getName()) || request.getTarget().equalsIgnoreCase("PROXY") && CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().getType() == ServiceType.PROXY || request.getTarget().equalsIgnoreCase("BUKKIT") && CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().getType() == ServiceType.SPIGOT) {
                        for (Consumer<DriverRequest<?>> requestHandler : requestHandlers) {
                            requestHandler.accept(request);
                        }
                    }
                }

            }
        });


        CloudDriver.getInstance().getMessageManager().registerChannel("API_RESPONSES", new Consumer<IChannelMessage>() {

            @Override @SneakyThrows
            public void accept(IChannelMessage channelMessage) {
                JsonObject<?> document = channelMessage.getDocument().getObject("response");
                DriverResponse<?> response = new DriverResponseObject<>();
                response.id(document.getString("id"));
                response.success(document.getBoolean("success"));
                response.error(document.getElement("error").isJsonNull() ? null : document.get("error", DriverRequestException.class));
                response.typeClass(Class.forName(document.getString("typeClass")));

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

                SimpleQuery<?> future = (SimpleQuery<?>) retrieveFuture(response.getId());
                if (future == null) {
                    return;
                }
                DriverRequest<?> request = future.getRequest();
                request.typeClass(response.typeClass());
                future.setRequest(request);
                future.completeFuture(response);
            }
        });
    }


    /**
     * Registers {@link DriverRequest}-handler
     *
     * @param requestHandler the handler
     */
    public final void registerRequestHandler(Consumer<DriverRequest<?>> requestHandler) {
        this.requestHandlers.add(requestHandler);
    }


    /**
     * Adds a {@link IQuery} to the cache with a given id
     *
     * @param id the id of the request
     * @param future the future
     */
    public void addRequest(String id, IQuery<?> future) {
        futures.put(id, future);
    }

    /**
     * Gets an {@link IQuery} from cache
     * and then automatically removes it from cache
     *
     * @param id the id
     * @return future or null
     */
    public IQuery<?> retrieveFuture(String id) {
        IQuery<?> future = futures.get(id);
        futures.remove(id);
        return future;
    }

    /**
     * Transforms an {@link IQuery} to a {@link IChannelMessage}
     * to be able to communicate around the network
     *
     * @param request the request
     * @return message
     */
    public IChannelMessage toMessage(DriverRequest<?> request) {
        Identifiable target;
        if (request.getTarget() == null || request.getTarget().equalsIgnoreCase("ALL") || request.getTarget().equalsIgnoreCase("BUKKIT") || request.getTarget().equalsIgnoreCase("PROXY")) {
            target = Identifiable.ALL;
        } else {
            target = CloudDriver.getInstance().getServiceManager().getCachedObject(request.getTarget());
        }
        return IChannelMessage.builder().channel("API_REQUESTS").receiver(target).document(new JsonDocument().append("request", request)).build();
    }

    /**
     * Transforms an {@link DriverResponse} to a {@link IChannelMessage}
     * to be able to communicate around the network
     *
     * @param response the response
     * @return message
     */
    public IChannelMessage toMessage(DriverResponse<?> response) {
        return IChannelMessage.builder().channel("API_RESPONSES").document(JsonObject.gson().append("response", response)).build();
    }
}