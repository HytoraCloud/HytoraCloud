package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInCopyTemplate;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInCreateTemplate;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter @AllArgsConstructor
public class Templates {

    private final CloudAPI cloudAPI;

    /**
     * Copies a server into a specific Template
     * @param server
     * @param template
     */
    public void copy(String server, String template) {
        PacketInCopyTemplate packetInCopyTemplate = new PacketInCopyTemplate(this.cloudAPI.getNetwork().getService(server), template);
        CloudAPI.getInstance().getCloudClient().sendPacket(packetInCopyTemplate);
    }

    /**
     * Creates a Template for a group
     * @param group
     * @param template
     */
    public void create(ServiceGroup group, String template) {
        PacketInCreateTemplate packetInCreateTemplate = new PacketInCreateTemplate(group, template);
        CloudAPI.getInstance().getCloudClient().sendPacket(packetInCreateTemplate);
    }

}
