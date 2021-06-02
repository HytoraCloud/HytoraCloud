package de.lystx.hytoracloud.driver.elements.packets.request.perms;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestPermissionGroupGet extends Packet {

    private UUID playerUUID;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeUUID(playerUUID);
    }

    @Override
    public void read(PacketBuffer buf) {
        playerUUID = buf.readUUID();
    }
}
