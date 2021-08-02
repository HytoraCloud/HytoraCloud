package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInNPCCreate extends Packet {

    private NPCMeta meta;

    @Override
    public void write(Component component) {
        component.put("meta", meta);
    }

    @Override
    public void read(Component component) {
        meta = component.get("meta");
    }
}
