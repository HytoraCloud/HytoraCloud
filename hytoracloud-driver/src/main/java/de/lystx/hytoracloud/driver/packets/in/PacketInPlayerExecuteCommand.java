package de.lystx.hytoracloud.driver.packets.in;



import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;



import java.io.Serializable;

/**
 * This packet just shows
 * that a player has executed a
 * command and you can get the whole
 * command line the player executed
 */
@Getter @AllArgsConstructor
public class PacketInPlayerExecuteCommand extends JsonPacket {

    @PacketSerializable
    private String player;

    @PacketSerializable
    private String command;

}
