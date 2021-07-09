package net.hytora.networking.elements.other;

import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.function.Consumer;

public interface ComponentSender {

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
     * Sends a {@link HytoraPacket} to the other
     * connection end (Server -> client | Client -> Server)
     *
     * @param packet the packet to send
     */
    default void sendPacket(HytoraPacket packet) {
        this.sendComponent(component -> {
            component.setChannel("_packets");
            component.append(map -> {
                map.put("_class", packet.getClass().getName());
                map.put("_uuid", packet.getPacketUUID());
                map.put("_ms", System.currentTimeMillis());
            });

            if (this instanceof HytoraServer) {
                component.setReceiver("all");
            }

            packet.write(component);
        });
    }

    /**
     * Gets the name of this sender
     *
     * @return name
     */
    String getName();

}
