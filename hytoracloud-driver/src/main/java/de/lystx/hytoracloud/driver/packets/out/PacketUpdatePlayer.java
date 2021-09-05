package de.lystx.hytoracloud.driver.packets.out;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.wrapped.PlayerObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;




@Getter @AllArgsConstructor @Setter
public class PacketUpdatePlayer extends JsonPacket {


    @PacketSerializable(PlayerObject.class)
    private ICloudPlayer cloudPlayer;

}
