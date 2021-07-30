package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketRequestPlayerUniqueId extends Packet {

    private UUID uniqueId;

    @Override
    public void write(Component component) {
        component.put("uuid", uniqueId);
    }

    @Override
    public void read(Component component) {
        uniqueId = component.get("uuid");
    }
}
