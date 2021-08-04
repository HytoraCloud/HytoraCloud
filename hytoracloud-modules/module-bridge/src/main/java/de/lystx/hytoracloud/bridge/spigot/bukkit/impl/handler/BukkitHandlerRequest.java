package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.wrapped.ServiceInfoObject;



import java.util.function.Consumer;

public class BukkitHandlerRequest implements IPacketHandler {

    public BukkitHandlerRequest() {
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
                }
            }
        });
    }

    @Override
    public void handle(IPacket packet) {
    }

}
