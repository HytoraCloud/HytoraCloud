package de.lystx.hytoracloud.driver.elements.packets.request.property;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestGetProperty extends Packet {

    private UUID playerUUID;
    private String name;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeUUID(playerUUID);
        buf.writeString(name);
    }

    @Override
    public void read(PacketBuffer buf) {
        playerUUID = buf.readUUID();
        name = buf.readString();
    }
}
