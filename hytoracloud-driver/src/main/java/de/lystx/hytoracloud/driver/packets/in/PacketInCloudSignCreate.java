package de.lystx.hytoracloud.driver.packets.in;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.serverselector.sign.CloudSign;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketInCloudSignCreate extends JsonPacket {

    @PacketSerializable
    private CloudSign cloudSign;

}
