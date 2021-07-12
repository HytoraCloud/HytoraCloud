package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class CloudHandlerGroupUpdate implements PacketHandler {

    private final CloudSystem cloudSystem;

    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketInUpdateServiceGroup) {
            PacketInUpdateServiceGroup packetInUpdateServiceGroup = (PacketInUpdateServiceGroup)packet;
            IServiceGroup group = packetInUpdateServiceGroup.getIServiceGroup();

            this.cloudSystem.getInstance(GroupService.class).updateGroup(group);
            CloudDriver.getInstance().getServiceManager().updateGroup(group);
            this.cloudSystem.reload();
        }
    }
}
