package de.lystx.cloudsystem.cloud.handler.group;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInCreateTemplate;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;

public class PacketHandlerCopyTemplate extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerCopyTemplate(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInCreateTemplate) {
            PacketPlayInCreateTemplate packetPlayInCreateTemplate = (PacketPlayInCreateTemplate)packet;
            ServiceGroup group = packetPlayInCreateTemplate.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getService(GroupService.class).getGroup(group.getName());
            if (get == null) {
                return;
            }
            this.cloudSystem.getService(TemplateService.class).createTemplate(get, packetPlayInCreateTemplate.getTemplate());
        }
    }
}
