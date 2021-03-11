package de.lystx.cloudsystem.cloud.handler.services;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInStartGroup;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInStartGroupWithProperties;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInStartService;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerStart extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerStart(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInStartGroup) {
            PacketInStartGroup packetInStartGroup = (PacketInStartGroup) packet;
            ServiceGroup group = packetInStartGroup.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getService(GroupService.class).getGroup(group.getName());
            if (get == null) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cCouldn't find group for §e" + group.getName() + "§c!");
                return;
            }
            this.cloudSystem.getService().startService(get);
        } else if (packet instanceof PacketInStartGroupWithProperties) {
            PacketInStartGroupWithProperties packetPlayInStartGroup = (PacketInStartGroupWithProperties) packet;
            ServiceGroup group = packetPlayInStartGroup.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getService(GroupService.class).getGroup(group.getName());
            if (get == null) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cCouldn't find group for §e" + group.getName() + "§c!");
                return;
            }
            this.cloudSystem.getService().startService(get, packetPlayInStartGroup.getProperties());
        } else if (packet instanceof PacketInStartService) {
            PacketInStartService packetInStartService = (PacketInStartService)packet;
            Service service = packetInStartService.getService();
            this.cloudSystem.getService().startService(service.getServiceGroup(), service, packetInStartService.getProperties());
        }
    }
}
