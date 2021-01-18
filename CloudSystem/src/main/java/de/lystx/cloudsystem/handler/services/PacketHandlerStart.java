package de.lystx.cloudsystem.handler.services;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStartGroup;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStartGroupWithProperties;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStartService;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.elements.other.Document;

public class PacketHandlerStart extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerStart(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInStartGroup) {
            PacketPlayInStartGroup packetPlayInStartGroup = (PacketPlayInStartGroup) packet;
            ServiceGroup group = packetPlayInStartGroup.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getService(GroupService.class).getGroup(group.getName());
            if (get == null) {
                return;
            }
            this.cloudSystem.getService().startService(get);
        } else if (packet instanceof PacketPlayInStartGroupWithProperties) {
            PacketPlayInStartGroupWithProperties packetPlayInStartGroup = (PacketPlayInStartGroupWithProperties) packet;
            ServiceGroup group = packetPlayInStartGroup.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getService(GroupService.class).getGroup(group.getName());
            if (get == null) {
                return;
            }
            this.cloudSystem.getService().startService(get);
        } else if (packet instanceof PacketPlayInStartService) {
            PacketPlayInStartService packetPlayInStartService = (PacketPlayInStartService)packet;
            Service service = packetPlayInStartService.getService();
            Document properties = packetPlayInStartService.getProperties();
            this.cloudSystem.getService().startService(service.getServiceGroup(), service, properties);
        }
    }
}
