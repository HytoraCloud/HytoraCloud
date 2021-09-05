package de.lystx.hytoracloud.driver.packets.both.other;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.forwarding.ForwardingPacketJson;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @Getter @AllArgsConstructor
public class PacketUpdatePermissionPool extends ForwardingPacketJson {

    @PacketSerializable
    private PermissionPool permissionPool;


}
