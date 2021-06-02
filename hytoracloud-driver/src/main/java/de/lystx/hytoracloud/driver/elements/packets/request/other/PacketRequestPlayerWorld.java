package de.lystx.hytoracloud.driver.elements.packets.request.other;

import de.lystx.hytoracloud.driver.elements.packets.UUIDPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PacketRequestPlayerWorld extends UUIDPacket {

    public PacketRequestPlayerWorld(UUID uuid) {
        super(uuid);
    }
}
