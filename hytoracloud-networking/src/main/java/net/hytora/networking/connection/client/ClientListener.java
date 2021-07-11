package net.hytora.networking.connection.client;

import net.hytora.networking.elements.component.ComponentSender;
import net.hytora.networking.elements.packet.HytoraPacket;

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
    void onReceive(ComponentSender sender, Object object);

    /**
     * Called when a packet comes in
     *
     * @param packet the packet
     */
    void packetIn(HytoraPacket packet);

    /**
     * Called when a packet is prepared to send
     *
     * @param packet the packet
     */
    void packetOut(HytoraPacket packet);
}
