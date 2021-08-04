package de.lystx.hytoracloud.cloud.handler.group;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketInCreateTemplate;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;

import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;
import lombok.AllArgsConstructor;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class CloudHandlerTemplateCopy implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInCreateTemplate) {
            PacketInCreateTemplate packetInCreateTemplate = (PacketInCreateTemplate)packet;
            IServiceGroup group = packetInCreateTemplate.getIServiceGroup();
            IServiceGroup get = CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).getCachedObject(group.getName());
            if (get == null) {
                return;
            }
            CloudDriver.getInstance().getTemplateManager().createTemplate(get, packetInCreateTemplate.getTemplate());
        }
    }
}
