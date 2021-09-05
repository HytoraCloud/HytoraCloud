package de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.forwarding;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.other.PacketBuffer;
import lombok.Getter;

import java.io.IOException;

@Getter
public class ForwardingPacketBuffer extends NettyPacket implements ForwardingPacket<ForwardingPacketBuffer> {

    private boolean forward = true;

    @Override
    public ForwardingPacketBuffer forward(boolean forward) {
        this.forward = forward;
        return this;
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        this.forward = buf.readBoolean();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeBoolean(this.forward);
    }

}
