package de.lystx.hytoracloud.driver.packets.both.player;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.forwarding.ForwardingPacketJson;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketSendMessage extends ForwardingPacketJson {

    @PacketSerializable
    private UUID uuid;

    @PacketSerializable
    private String message;

}
