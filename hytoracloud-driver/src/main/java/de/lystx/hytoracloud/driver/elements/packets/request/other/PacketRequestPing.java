package de.lystx.hytoracloud.driver.elements.packets.request.other;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestPing extends Packet {

    private UUID uuid;
    private String key;


    public PacketRequestPing(UUID uuid) {
        this(uuid, "no key");
    }


    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.nullSafe().writeUUID(uuid);
        packetBuffer.nullSafe().writeString(key);
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        uuid = packetBuffer.nullSafe().readUUID();
        key = packetBuffer.nullSafe().readString();
    }
}
