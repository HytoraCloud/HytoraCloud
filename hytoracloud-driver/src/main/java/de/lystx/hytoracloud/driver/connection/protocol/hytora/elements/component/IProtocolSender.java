package de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.connection.server.NetworkServer;

import java.io.Serializable;
import java.util.function.Consumer;

public interface IProtocolSender {

    /**
     * Sends a {@link Component} to the other
     * connection end (Server -> client | Client -> Server)
     *
     * @param consumer the consumer
     */
    void sendComponent(Consumer<Component> consumer);

    /**
     * Sends a {@link Component} raw without consumer
     *
     * @param component the component
     */
    void sendComponent(Component component);

    /**
     * Sends a {@link Packet} to the other
     * connection end (Server -> client | Client -> Server)
     *
     * @param packet the packet to send
     */
    default void sendPacket(Packet packet) {
        Component component = this.packetToComponent(packet);
        this.sendComponent(component);
    }

    /**
     * Sends a {@link Packet} to a given receiver
     *
     * @param packet the packet
     * @param receiver the receiver
     */
    default void sendPacket(Packet packet, String receiver) {
        Component component = this.packetToComponent(packet);
        component.setReceiver(receiver);
        this.sendComponent(component);
    }

    /**
     * Transforms a packet into a component
     *
     * @param packet the packet
     * @return component
     */
    default Component packetToComponent(Packet packet) {
        Component component = new Component();
        component.setChannel("_packets");
        component.append(map -> {
            map.put("_class", packet.getClass().getName());
            map.put("_uuid", packet.getPacketUUID());
            map.put("_ms", System.currentTimeMillis());
        });

        if (this instanceof NetworkServer) {
            component.setReceiver("ALL");
        } else {
            component.setReceiver("SERVER");
        }

        packet.write(component);

        return component;
    }

    /**
     * Sends an object which is {@link Serializable}
     *
     * @param object the object
     */
    void sendObject(Serializable object);

    /**
     * Gets the name of this sender
     *
     * @return name
     */
    String getName();

}
