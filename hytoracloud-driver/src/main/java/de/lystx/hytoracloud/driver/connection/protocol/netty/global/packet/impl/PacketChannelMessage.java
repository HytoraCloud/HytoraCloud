package de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl;

import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.wrapped.ChannelMessageObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter @AllArgsConstructor
public class PacketChannelMessage extends NettyPacket {

    @PacketSerializable(ChannelMessageObject.class)
    private IChannelMessage channelMessage;

    @Override
    public void read(PacketBuffer buf) throws IOException {
        channelMessage = buf.readCustom(ChannelMessageObject.class);
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeCustom(this.channelMessage);
    }
}
