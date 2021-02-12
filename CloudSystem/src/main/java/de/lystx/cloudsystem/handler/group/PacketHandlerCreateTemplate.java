package de.lystx.cloudsystem.handler.group;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInCopyTemplate;
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
        if (packet instanceof PacketPlayInCopyTemplate) {
            PacketPlayInCopyTemplate packetPlayInCopyTemplate = (PacketPlayInCopyTemplate)packet;
            Service service = packetPlayInCopyTemplate.getService();
            Service get = this.cloudSystem.getService().getService(service.getName());
            if (get == null) {
                return;
            }
            this.cloudSystem.getService(TemplateService.class).copy(get, packetPlayInCopyTemplate.getTemplate());
        }
    }
}
