package de.lystx.hytoracloud.driver.elements.packets.request.perms;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestPermissionGroup extends Packet {

    private UUID name;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeUUID(name);
    }

    @Override
    public void read(PacketBuffer buf) {
        name = buf.readUUID();
    }
}
