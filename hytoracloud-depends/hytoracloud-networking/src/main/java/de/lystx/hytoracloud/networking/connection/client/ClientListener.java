package de.lystx.hytoracloud.networking.connection.client;

import de.lystx.hytoracloud.networking.elements.component.ComponentSender;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

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
    void packetIn(Packet packet);

    /**
     * Called when a packet is prepared to send
     *
     * @param packet the packet
     */
    void packetOut(Packet packet);
}
