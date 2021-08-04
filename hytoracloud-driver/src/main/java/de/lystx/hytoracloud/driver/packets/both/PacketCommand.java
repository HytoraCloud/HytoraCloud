package de.lystx.hytoracloud.driver.packets.both;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketCommand extends JsonPacket {

    @PacketSerializable
    private String service;

    @PacketSerializable
    private String command;


}
