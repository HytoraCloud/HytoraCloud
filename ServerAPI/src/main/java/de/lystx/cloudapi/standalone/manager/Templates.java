package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInCopyTemplate;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInCreateTemplate;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Templates {

    private final CloudAPI cloudAPI;

    public Templates(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    public void copy(String server, String template) {
        PacketPlayInCopyTemplate packetPlayInCopyTemplate = new PacketPlayInCopyTemplate(this.cloudAPI.getNetwork().getService(server), template);
        CloudAPI.getInstance().getCloudClient().sendPacket(packetPlayInCopyTemplate);
    }

    public void create(ServiceGroup group, String template) {
        PacketPlayInCreateTemplate packetPlayInCreateTemplate = new PacketPlayInCreateTemplate(group, template);
        CloudAPI.getInstance().getCloudClient().sendPacket(packetPlayInCreateTemplate);
    }

}
