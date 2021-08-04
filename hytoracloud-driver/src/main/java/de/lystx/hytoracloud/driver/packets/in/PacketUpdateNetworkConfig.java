package de.lystx.hytoracloud.driver.packets.in;

import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketUpdateNetworkConfig extends JsonPacket {

    @PacketSerializable
    private NetworkConfig networkConfig;

}
