package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.elements.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.server.impl.GroupService;

public class PacketHandlerGroupUpdate implements PacketHandler {

    private final CloudSystem cloudSystem;

    public PacketHandlerGroupUpdate(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    
    public void handle(Packet packet) {
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
