package de.lystx.cloudsystem.cloud.handler.group;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInCopyTemplate;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;

public class PacketHandlerCreateTemplate extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerCreateTemplate(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInCopyTemplate) {
            PacketInCopyTemplate packetInCopyTemplate = (PacketInCopyTemplate)packet;
            Service service = packetInCopyTemplate.getService();
            Service get = this.cloudSystem.getService().getService(service.getName());
            if (get == null) {
                return;
            }
            this.cloudSystem.getService(TemplateService.class).copy(get, packetInCopyTemplate.getTemplate(), packetInCopyTemplate.getSpecificDirectory());
        }
    }
}
