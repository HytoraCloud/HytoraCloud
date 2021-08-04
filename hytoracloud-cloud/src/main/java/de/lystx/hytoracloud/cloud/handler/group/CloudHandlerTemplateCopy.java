package de.lystx.hytoracloud.cloud.handler.group;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.in.PacketInCreateTemplate;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;

import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;
import lombok.AllArgsConstructor;



@AllArgsConstructor
public class CloudHandlerTemplateCopy implements IPacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketInCreateTemplate) {
            PacketInCreateTemplate packetInCreateTemplate = (PacketInCreateTemplate)packet;
            IServiceGroup group = packetInCreateTemplate.getServiceGroup();
            IServiceGroup get = CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).getCachedObject(group.getName());
            if (get == null) {
                return;
            }
            CloudDriver.getInstance().getTemplateManager().createTemplate(get, packetInCreateTemplate.getTemplate());
        }
    }
}
