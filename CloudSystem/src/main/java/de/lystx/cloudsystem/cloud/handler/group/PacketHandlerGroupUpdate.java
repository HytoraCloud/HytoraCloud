package de.lystx.cloudsystem.cloud.handler.group;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInUpdateServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;

public class PacketHandlerGroupUpdate extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerGroupUpdate(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInUpdateServiceGroup) {
            PacketInUpdateServiceGroup packetInUpdateServiceGroup = (PacketInUpdateServiceGroup)packet;
            ServiceGroup group = packetInUpdateServiceGroup.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getService(GroupService.class).getGroup(group.getName());
            this.cloudSystem.getService(GroupService.class).updateGroup(get, group);
            this.cloudSystem.getService().updateGroup(get, group);
            this.cloudSystem.reload();
        }
    }
}
