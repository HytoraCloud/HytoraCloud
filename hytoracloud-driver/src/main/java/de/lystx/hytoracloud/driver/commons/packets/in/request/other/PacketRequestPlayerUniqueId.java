package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketRequestPlayerUniqueId extends HytoraPacket {

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
