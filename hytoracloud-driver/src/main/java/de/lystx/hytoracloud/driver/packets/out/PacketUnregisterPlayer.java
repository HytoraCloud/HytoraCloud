package de.lystx.hytoracloud.driver.packets.out;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketUnregisterPlayer extends JsonPacket {

    @PacketSerializable
    private String name;

}
