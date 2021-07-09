package de.lystx.hytoracloud.driver.elements.packets.request.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestGetProperty extends HytoraPacket {

    private UUID playerUUID;
    private String name;

    @Override
    public void write(Component component) {
        component.put("uuid", playerUUID);
        component.put("name", name);
    }

    @Override
    public void read(Component component) {
        playerUUID = component.get("uuid");
        name = component.get("name");
    }
}
