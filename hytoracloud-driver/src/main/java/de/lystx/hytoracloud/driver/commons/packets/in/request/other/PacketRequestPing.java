package de.lystx.hytoracloud.driver.commons.packets.in.request.other;



import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestPing extends HytoraPacket {

    private UUID uuid;
    private String key;


    public PacketRequestPing(UUID uuid) {
        this(uuid, "no key");
    }


    @Override
    public void write(Component component) {
        component.put("uuid", uuid).put("key", key);
    }

    @Override
    public void read(Component component) {
        uuid = component.get("uuid");
        key = component.get("key");
    }
}
