package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestTPS;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import java.util.function.Consumer;

public class BukkitHandlerTPS implements PacketHandler {

    public BukkitHandlerTPS() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                if (driverRequest.equalsIgnoreCase("SERVICE_GET_TPS")) {
                    driverRequest.createResponse().data(CloudDriver.getInstance().getBridgeInstance().loadTPS()).send();
                }
            }
        });
    }

    public void handle(Packet packet) {

       if (packet instanceof PacketRequestTPS) {
            PacketRequestTPS packetRequestTPS = (PacketRequestTPS)packet;

            if (packetRequestTPS.getServer().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getCurrentService().getName())) {

                packet.reply(component -> component.put("tps", CloudDriver.getInstance().getBridgeInstance().loadTPS()));

            }
        }
    }


}
