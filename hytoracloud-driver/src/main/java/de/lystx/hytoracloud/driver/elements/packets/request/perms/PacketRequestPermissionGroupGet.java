package de.lystx.hytoracloud.driver.elements.packets.request.perms;

import de.lystx.hytoracloud.driver.elements.packets.UUIDPacket;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestPermissionGroupGet extends UUIDPacket {

    public PacketRequestPermissionGroupGet(UUID uuid) {
        super(uuid);
    }
}
