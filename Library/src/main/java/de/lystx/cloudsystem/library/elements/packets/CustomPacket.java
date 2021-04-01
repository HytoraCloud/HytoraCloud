package de.lystx.cloudsystem.library.elements.packets;

import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCommunication;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.netty.NettyClient;
import de.lystx.cloudsystem.library.service.network.netty.NettyServer;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.Getter;

import java.io.Serializable;

/**
 * This class is used to send a custom packet
 * without destroying Netty because Netty
 * only allows to flush objects from the same package
 * as the {@link NettyServer} or the {@link NettyClient}
 * to handle a custom packet you must use the {@link PacketHandler} annotation
 * just add
 <hr><blockquote><pre>
 *  @ {@link PacketHandler}(transformTo = PacketToHandle.class)
 *  public void handle(PacketToHandle packet) {
 *       //DO WHAT YOU WANT
 *  }
 * </pre></blockquote><hr>
 * <p>
 * But you can send it the normal way
 * <blockquote><pre>
 *     PacketToHandle yourPacket = ....
 *     CloudAPI.getInstance().sendPacket(yourPacket);
 * </pre></blockquote>
 */
@Getter
public class CustomPacket extends PacketCommunication implements Serializable {

    private final String packetClass;

     public CustomPacket(Packet packet) {
         this.packetClass = packet.getClass().getName();
         this.append("packet", packet);
     }
}
