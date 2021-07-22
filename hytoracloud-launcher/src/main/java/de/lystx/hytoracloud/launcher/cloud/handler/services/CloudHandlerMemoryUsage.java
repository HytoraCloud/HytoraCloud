package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceInfo;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMemoryUsage;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMinecraftInfo;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class CloudHandlerMemoryUsage implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(HytoraPacket packet) {

        if (packet instanceof PacketServiceMemoryUsage) {

            Component component = packet.toReply(CloudDriver.getInstance().getConnection());

            packet.reply(component.reply().getStatus(), component.reply().getMessage());
        } else if (packet instanceof PacketServiceInfo) {

            Component component = packet.toReply(CloudDriver.getInstance().getConnection());

            packet.reply(component);
        } else if (packet instanceof PacketServiceMinecraftInfo) {

            Component component = packet.toReply(CloudDriver.getInstance().getConnection());

            packet.reply(component);
        }
    }
}
