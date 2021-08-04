package de.lystx.hytoracloud.driver.packets.both.player;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.forwarding.ForwardingPacketJson;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.minecraft.chat.ChatComponent;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter  @AllArgsConstructor
public class PacketSendComponent extends ForwardingPacketJson {

    @PacketSerializable
    private UUID uuid;

    @PacketSerializable
    private ChatComponent chatComponent;


}
