package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInCreateTemplate;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import io.thunder.packet.handler.PacketHandler;

import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.server.impl.GroupService;
import de.lystx.hytoracloud.driver.service.server.impl.TemplateService;

public class PacketHandlerCopyTemplate implements PacketHandler {

    private final CloudSystem cloudSystem;

    public PacketHandlerCopyTemplate(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    
    public void handle(Packet packet) {
        if (packet instanceof PacketInCreateTemplate) {
            PacketInCreateTemplate packetInCreateTemplate = (PacketInCreateTemplate)packet;
            ServiceGroup group = packetInCreateTemplate.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getInstance(GroupService.class).getGroup(group.getName());
            if (get == null) {
                return;
            }
            this.cloudSystem.getInstance(TemplateService.class).createTemplate(get, packetInCreateTemplate.getTemplate());
        }
    }
}
