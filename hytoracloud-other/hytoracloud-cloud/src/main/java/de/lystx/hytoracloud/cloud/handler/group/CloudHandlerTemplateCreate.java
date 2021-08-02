package de.lystx.hytoracloud.cloud.handler.group;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCopyTemplate;
import de.lystx.hytoracloud.driver.commons.service.IService;

@AllArgsConstructor
public class CloudHandlerTemplateCreate implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(Packet packet) {
        if (packet instanceof PacketInCopyTemplate) {
            PacketInCopyTemplate packetInCopyTemplate = (PacketInCopyTemplate)packet;
            IService IService = packetInCopyTemplate.getIService();
            IService get = CloudDriver.getInstance().getServiceManager().getCachedObject(IService.getName());
            if (get == null) {
                return;
            }
            CloudDriver.getInstance().getTemplateManager().copyTemplate(get, packetInCopyTemplate.getTemplate(), packetInCopyTemplate.getSpecificDirectory());
        }
    }
}
