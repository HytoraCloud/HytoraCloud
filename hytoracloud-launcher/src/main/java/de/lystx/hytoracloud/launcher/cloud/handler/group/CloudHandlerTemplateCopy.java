package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCreateTemplate;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;

import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.TemplateService;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class CloudHandlerTemplateCopy implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketInCreateTemplate) {
            PacketInCreateTemplate packetInCreateTemplate = (PacketInCreateTemplate)packet;
            IServiceGroup group = packetInCreateTemplate.getIServiceGroup();
            IServiceGroup get = this.cloudSystem.getInstance(GroupService.class).getGroup(group.getName());
            if (get == null) {
                return;
            }
            this.cloudSystem.getInstance(TemplateService.class).createTemplate(get, packetInCreateTemplate.getTemplate());
        }
    }
}
