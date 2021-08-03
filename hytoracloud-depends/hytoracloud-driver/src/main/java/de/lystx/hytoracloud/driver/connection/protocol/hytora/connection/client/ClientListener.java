package de.lystx.hytoracloud.driver.connection.protocol.hytora.connection.client;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.IProtocolSender;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

import java.net.InetSocketAddress;

public interface ClientListener {

    /**
     * Called when the client connects to the server
     *
     * @param socketAddress the address of the server
     */
    void onConnect(InetSocketAddress socketAddress);

    /**
     * Called when the client receives the handshake
     * from the server and is now verified
     *
     */
    void onHandshake();

    /**
     * Called when disconnected
     */
    void onDisconnect();

    /**
     * Called when an object is received
     *
     * @param sender the sender
     * @param object the object
     */
    void onReceive(IProtocolSender sender, Object object);

    /**
     * Called when a packet comes in
     *
     * @param packet the packet
     */
    void packetIn(Packet packet);

    /**
     * Called when a packet is prepared to send
     *
     * @param packet the packet
     */
    void packetOut(Packet packet);
}
