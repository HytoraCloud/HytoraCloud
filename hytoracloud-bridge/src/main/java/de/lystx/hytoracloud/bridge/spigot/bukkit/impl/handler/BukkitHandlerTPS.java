package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestTPS;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

public class BukkitHandlerTPS implements PacketHandler {

    public void handle(Packet packet) {

       if (packet instanceof PacketRequestTPS) {
            PacketRequestTPS packetRequestTPS = (PacketRequestTPS)packet;

            if (packetRequestTPS.getServer().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {

                packet.reply(component -> component.put("tps", CloudDriver.getInstance().getBridgeInstance().loadTPS()));

            }
        }
    }


}
