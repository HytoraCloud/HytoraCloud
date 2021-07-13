package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMemoryUsage;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroupWithProperties;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartService;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;

@AllArgsConstructor
public class CloudHandlerMemoryUsage implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(HytoraPacket packet) {

        if (packet instanceof PacketServiceMemoryUsage) {

            Component component = packet.toReply(CloudDriver.getInstance().getConnection());

            packet.reply(ResponseStatus.SUCCESS, component.reply().getMessage());
        }
    }
}
