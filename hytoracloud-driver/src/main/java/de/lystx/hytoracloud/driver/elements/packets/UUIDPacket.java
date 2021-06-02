package de.lystx.hytoracloud.driver.elements.packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;

import java.util.UUID;


public class UUIDPacket extends Packet {

    private UUID uuid;

    public UUIDPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public final void write(PacketBuffer buf) {
        buf.writeUUID(uuid);
    }

    @Override
    public final void read(PacketBuffer buf) {
        uuid = buf.readUUID();
    }
}
