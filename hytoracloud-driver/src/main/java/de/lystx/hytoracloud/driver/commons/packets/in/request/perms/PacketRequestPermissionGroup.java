package de.lystx.hytoracloud.driver.commons.packets.in.request.perms;

import de.lystx.hytoracloud.driver.commons.packets.UUIDPacket;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestPermissionGroup extends UUIDPacket {

    public PacketRequestPermissionGroup(UUID uuid) {
        super(uuid);
    }
}
