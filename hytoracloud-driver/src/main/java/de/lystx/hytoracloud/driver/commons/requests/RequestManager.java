package de.lystx.hytoracloud.driver.commons.requests;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequestFuture;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverResponse;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverResponseObject;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverFutureObject;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequestObject;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;

import java.util.*;
import java.util.function.Consumer;

public class RequestManager {

    /**
     * All stored futures with their ids
     */
    private final Map<String, DriverRequestFuture<?>> futures;

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

                for (Consumer<DriverRequest<?>> requestHandler : requestHandlers) {
                    if (request.getTarget() == null || request.getTarget().equalsIgnoreCase("ALL")) {
                        requestHandler.accept(request);
                    }
                    if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
                        if (request.getTarget() == null || request.getTarget().equalsIgnoreCase("CLOUD")) {
                            requestHandler.accept(request);
                        } else {
                            DriverRequestFuture<?> comply = request.comply();
                            request.createResponse(request.typeClass()).data(comply.pullValue()).send();
                        }
                    } else {
                        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
                            if (request.getTarget() == null || request.getTarget().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName()) || request.getTarget().equalsIgnoreCase("PROXY") && CloudDriver.getInstance().getCurrentService().getGroup().getType() == ServiceType.PROXY || request.getTarget().equalsIgnoreCase("BUKKIT") && CloudDriver.getInstance().getCurrentService().getGroup().getType() == ServiceType.SPIGOT) {
                                requestHandler.accept(request);
                            }
                        }
                    }
                }

            }
        });


        CloudDriver.getInstance().getMessageManager().registerChannel("API_RESPONSES", new Consumer<IChannelMessage>() {

            @Override
            public void accept(IChannelMessage channelMessage) {
                DriverResponse<?> response = channelMessage.getDocument().get("response", DriverResponseObject.class);

                DriverFutureObject<?> future = (DriverFutureObject<?>) retrieveFuture(response.getId());
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
     * Registers {@link DriverRequest}-handlers
     *
     * @param requestHandlers the handlers
     */
    @SafeVarargs
    public final void registerRequestHandler(Consumer<DriverRequest<?>>... requestHandlers) {
        this.requestHandlers.addAll(Arrays.asList(requestHandlers));
    }


    /**
     * Adds a {@link DriverRequestFuture} to the cache with a given id
     *
     * @param id the id of the request
     * @param future the future
     */
    public void addRequest(String id, DriverRequestFuture<?> future) {
        futures.put(id, future);
    }

    /**
     * Gets an {@link DriverRequestFuture} from cache
     * and then automatically removes it from cache
     *
     * @param id the id
     * @return future or null
     */
    public DriverRequestFuture<?> retrieveFuture(String id) {
        DriverRequestFuture<?> future = futures.get(id);
        futures.remove(id);
        return future;
    }

    /**
     * Transforms an {@link DriverRequestFuture} to a {@link IChannelMessage}
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
