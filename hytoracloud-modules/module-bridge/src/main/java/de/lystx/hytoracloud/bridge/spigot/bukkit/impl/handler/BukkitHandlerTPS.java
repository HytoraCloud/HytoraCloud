package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceInfoObject;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import java.util.function.Consumer;

public class BukkitHandlerTPS implements PacketHandler {

    public BukkitHandlerTPS() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                if (driverRequest.equalsIgnoreCase("SERVICE_GET_TPS")) {
                    driverRequest.createResponse().data(CloudDriver.getInstance().getBridgeInstance().loadTPS()).send();
                } else if (driverRequest.equalsIgnoreCase("SERVICE_SET_MOTD")) {
                    driverRequest.createResponse().data(CloudDriver.getInstance().getServiceManager().getThisService().setMotd(document.getString("motd")));
                } else if (driverRequest.equalsIgnoreCase("SERVICE_SET_MAX_PLAYERS")) {
                    driverRequest.createResponse().data(CloudDriver.getInstance().getServiceManager().getThisService().setMaxPlayers(document.getInteger("maxPlayers")));
                } else if (driverRequest.equalsIgnoreCase("SERVICE_UPDATE_INFO")) {
                    driverRequest.createResponse().data(CloudDriver.getInstance().getServiceManager().getThisService().setInfo(document.get("serviceInfo", ServiceInfoObject.class)));
                } else if (driverRequest.equalsIgnoreCase("SERVICE_SET_MAX_PLAYERS")) {
                }
            }
        });
    }

    @Override
    public void handle(Packet packet) {
    }


}
