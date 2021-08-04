package de.lystx.hytoracloud.driver.packets.receiver;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketReceiverScreenCache extends JsonPacket {


    @PacketSerializable
    private String screen;

    @PacketSerializable
    private String line;

}
