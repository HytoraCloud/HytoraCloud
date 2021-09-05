package de.lystx.hytoracloud.cloud.handler.services;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;


import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.response.ResponseStatus;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;


public class CloudHandlerUpdate implements IPacketHandler {

    public CloudHandlerUpdate() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                if (driverRequest.equalsIgnoreCase("SERVICE_GET_SYNC_UUID")) {
                    UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
                    IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(uniqueId);
                    driverRequest.createResponse().data(service).send();
                } else if (driverRequest.equalsIgnoreCase("SERVICE_GET_SYNC_NAME")) {
                    String name = document.getString("name");
                    IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(name);
                    driverRequest.createResponse().data(service).send();
                } else if (driverRequest.equalsIgnoreCase("SCREEN_GET_ALL")) {
                    List<IScreen> screens = CloudDriver.getInstance().getScreenManager().getScreens();
                    driverRequest.createResponse().data(screens).send();
                } else {
                    if (!document.has("name")) {
                        return;
                    }
                    String name = document.getString("name");
                    IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(name);
                    if (service == null) {
                        return;
                    }
                    if (driverRequest.equalsIgnoreCase("SERVICE_SET_PROPERTIES")) {
                        driverRequest.createResponse().data(service.setProperties(JsonObject.serializable(document.getString("properties"))).pullValue()).send();

                    } else if (driverRequest.equalsIgnoreCase("SERVICE_ADD_PROPERTY")) {
                        driverRequest.createResponse().data(service.addProperty(document.getString("key"), JsonObject.serializable(document.getString("properties"))).pullValue()).send();

                    } else if (driverRequest.equalsIgnoreCase("SERVICE_SET_AUTHENTICATED")) {
                        driverRequest.createResponse().data(service.setAuthenticated(document.getBoolean("value")).pullValue()).send();

                    } else if (driverRequest.equalsIgnoreCase("SERVICE_SET_STATE")) {
                        try {
                            ServiceState state = ServiceState.valueOf(document.getString("state"));
                            driverRequest.createResponse().data(service.setState(state).pullValue()).send();
                        } catch (Exception e) {
                            driverRequest.createResponse().data(ResponseStatus.FAILED).exception(e).send();
                        }
                    } else if (driverRequest.equalsIgnoreCase("SERVICE_SET_HOST")) {

                        driverRequest.createResponse().data(service.setHost(document.getString("host")).pullValue()).send();

                    } else if (driverRequest.equalsIgnoreCase("SERVICE_VERIFYY")) {
                        JsonObject<PropertyObject> properties = JsonObject.serializable(document.getString("properties"));
                        String host = document.getString("host");
                        boolean verify = document.getBoolean("verified");
                        ServiceState state = ServiceState.valueOf(document.getString("state"));

                        service.setCachedAuthenticated(verify);
                        service.setCachedHost(host);
                        service.setCachedProperties(properties);
                        service.setCachedState(state);

                        CloudDriver.getInstance().getServiceManager().updateService(service);
                        CloudDriver.getInstance().reload();
                        driverRequest.createResponse().data(service).send();
                    } else if (driverRequest.equalsIgnoreCase("SCREEN_GET_NAME")) {
                        IScreen orRequest = CloudDriver.getInstance().getScreenManager().getOrRequest(name);
                        driverRequest.createResponse().data(orRequest).send();

                    }
                }
            }
        });
    }

    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            IService service = packetServiceUpdate.getService();
            CloudDriver.getInstance().getServiceManager().updateService(service);
        }
    }
}
