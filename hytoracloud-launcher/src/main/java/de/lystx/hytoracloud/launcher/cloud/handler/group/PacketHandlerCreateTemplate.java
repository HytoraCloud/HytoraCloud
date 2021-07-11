package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCopyTemplate;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.service.cloud.server.impl.TemplateService;

@AllArgsConstructor
public class PacketHandlerCreateTemplate implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(HytoraPacket packet) {
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
