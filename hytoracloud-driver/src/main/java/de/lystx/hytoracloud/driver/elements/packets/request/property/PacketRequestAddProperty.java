package de.lystx.hytoracloud.driver.elements.packets.request.property;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestAddProperty extends Packet {

    private UUID playerUUID;
    private String name;
    private JsonObject property;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeUUID(playerUUID);
        buf.writeString(name);
        buf.writeString(property.toString());
    }

    @Override
    public void read(PacketBuffer buf) {
        playerUUID = buf.readUUID();
        name = buf.readString();
        property = (JsonObject) new JsonParser().parse(buf.readString());
    }
}
