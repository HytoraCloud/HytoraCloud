package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;

import java.util.function.Consumer;


public class CloudHandlerUpdate implements PacketHandler {

    public CloudHandlerUpdate() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                if (!document.has("name")) {
                    return;
                }
                String name = document.getString("name");
                IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(name);

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
                        driverRequest.createResponse().error(new DriverRequestException("State could not be parsed", 0x10, e.getClass())).data(ResponseStatus.FAILED).send();
                    }
                } else if (driverRequest.equalsIgnoreCase("SERVICE_SET_HOST")) {
                    driverRequest.createResponse().data(service.setHost(document.getString("host")).pullValue()).send();

                }
            }
        });
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            IService service = packetServiceUpdate.getService();
            CloudDriver.getInstance().getServiceManager().updateService(service);
        }
    }
}
