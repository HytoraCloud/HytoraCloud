package de.lystx.hytoracloud.driver.commons.packets.in.request.other;



import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestPing extends Packet {

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
