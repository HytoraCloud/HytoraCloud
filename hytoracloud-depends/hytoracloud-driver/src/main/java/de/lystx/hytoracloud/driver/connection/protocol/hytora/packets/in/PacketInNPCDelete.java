package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.serverselector.npc.NPCMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInNPCDelete extends Packet {

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
