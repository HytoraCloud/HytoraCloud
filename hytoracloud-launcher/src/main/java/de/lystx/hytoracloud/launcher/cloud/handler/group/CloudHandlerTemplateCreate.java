package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCopyTemplate;
import de.lystx.hytoracloud.driver.commons.service.IService;

@AllArgsConstructor
public class CloudHandlerTemplateCreate implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(HytoraPacket packet) {
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
