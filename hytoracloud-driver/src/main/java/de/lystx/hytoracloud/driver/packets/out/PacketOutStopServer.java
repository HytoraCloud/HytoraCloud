package de.lystx.hytoracloud.driver.packets.out;



import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketOutStopServer extends JsonPacket {

    @PacketSerializable
    private String service;

}
