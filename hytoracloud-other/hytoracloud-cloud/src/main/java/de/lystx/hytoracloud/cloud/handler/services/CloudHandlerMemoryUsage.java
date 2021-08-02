package de.lystx.hytoracloud.cloud.handler.services;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMinecraftInfo;
import de.lystx.hytoracloud.cloud.CloudSystem;
import lombok.AllArgsConstructor;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class CloudHandlerMemoryUsage implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(Packet packet) {

        if (packet instanceof PacketServiceMinecraftInfo) {

            Component component = packet.toReply(CloudDriver.getInstance().getConnection());

            packet.reply(component);
        }
    }
}
