package de.lystx.hytoracloud.driver.packets.in;


import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;




@AllArgsConstructor @Getter
public class PacketInStopServer extends JsonPacket {


    @PacketSerializable
    private String service;

}
