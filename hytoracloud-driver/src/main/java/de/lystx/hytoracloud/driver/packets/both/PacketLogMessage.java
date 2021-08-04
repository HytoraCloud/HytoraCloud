package de.lystx.hytoracloud.driver.packets.both;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.forwarding.ForwardingPacketJson;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This packet is used to send
 * a message to the CloudSystem
 *
 * and you can choose if you only
 * want it to show up in the log or
 * in the console if you change showUpInConsole
 * to false
 */
@Getter @AllArgsConstructor
public class PacketLogMessage extends ForwardingPacketJson {

    @PacketSerializable
    private String prefix;

    @PacketSerializable
    private String message;

    @PacketSerializable
    private boolean showUpInConsole;

}
