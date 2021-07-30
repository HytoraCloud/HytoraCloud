package de.lystx.hytoracloud.driver.commons.packets;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import java.util.UUID;


@Getter @AllArgsConstructor @NoArgsConstructor
public class UUIDPacket extends Packet {

    private UUID uuid;

    @Override
    public void write(Component component) {
        component.put("uuid", uuid);
    }

    @Override
    public void read(Component component) {
        uuid = component.get("uuid");
    }
}
