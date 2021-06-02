package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.elements.packets.in.PacketInCopyTemplate;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.server.impl.TemplateService;

public class PacketHandlerCreateTemplate implements PacketHandler {

    private final CloudSystem cloudSystem;

    public PacketHandlerCreateTemplate(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    
    public void handle(Packet packet) {
        if (packet instanceof PacketInCopyTemplate) {
            PacketInCopyTemplate packetInCopyTemplate = (PacketInCopyTemplate)packet;
            Service service = packetInCopyTemplate.getService();
            Service get = CloudDriver.getInstance().getServiceManager().getService(service.getName());
            if (get == null) {
                return;
            }
            this.cloudSystem.getInstance(TemplateService.class).copy(get, packetInCopyTemplate.getTemplate(), packetInCopyTemplate.getSpecificDirectory());
        }
    }
}
