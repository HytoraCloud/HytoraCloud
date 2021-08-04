package de.lystx.hytoracloud.driver.connection.protocol.netty.messenger;

import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.wrapped.ChannelMessageObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketChannelMessage extends JsonPacket {

    @PacketSerializable(ChannelMessageObject.class)
    private IChannelMessage channelMessage;
}
