package de.lystx.hytoracloud.driver.elements.packets.request.perms;

import de.lystx.hytoracloud.driver.service.permission.impl.PermissionValidity;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@AllArgsConstructor @Getter
public class PacketRequestPermissionGroupAdd extends Packet {

    private UUID playerUUID;
    private String group;
    private int i;
    private PermissionValidity validality;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeUUID(playerUUID);
        buf.writeString(group);
        buf.writeInt(i);
        buf.writeEnum(validality);
    }

    @Override
    public void read(PacketBuffer buf) {
        playerUUID = buf.readUUID();
        group = buf.readString();
        i = buf.readInt();
        validality = buf.readEnum(PermissionValidity.class);
    }
}
