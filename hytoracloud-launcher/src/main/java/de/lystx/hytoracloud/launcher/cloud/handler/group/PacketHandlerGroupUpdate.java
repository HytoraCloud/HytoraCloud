package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.elements.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.server.impl.GroupService;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class PacketHandlerGroupUpdate implements PacketHandler {

    private final CloudSystem cloudSystem;

    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketInUpdateServiceGroup) {
            PacketInUpdateServiceGroup packetInUpdateServiceGroup = (PacketInUpdateServiceGroup)packet;
            ServiceGroup group = packetInUpdateServiceGroup.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getInstance(GroupService.class).getGroup(group.getName());
            this.cloudSystem.getInstance(GroupService.class).updateGroup(group);
            CloudDriver.getInstance().getServiceManager().updateGroup(get, group);
            this.cloudSystem.reload();
        }
    }
}
