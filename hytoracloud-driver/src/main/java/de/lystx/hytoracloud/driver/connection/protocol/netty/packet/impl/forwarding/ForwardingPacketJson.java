package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.forwarding;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import lombok.Getter;

@Getter
public class ForwardingPacketJson extends JsonPacket implements ForwardingPacket<ForwardingPacketJson>  {

    @PacketSerializable
    private boolean forward = true;

    @Override
    public ForwardingPacketJson forward(boolean b) {
        this.forward = b;
        return this;
    }
}
