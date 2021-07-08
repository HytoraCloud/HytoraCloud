package net.hytora.networking.connection;

import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.packet.PacketManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.other.UserManager;

import java.io.Closeable;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface HytoraConnection extends Closeable {

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
     * Sends a {@link Component} to the server
     *
     * In this method the reply is also included and will be accepted in the given consumer
     * If no reply is given or the reply timed out the boolean part of the consumer will be set to false
     *
     * @param componentConsumer the consumer for the component
     * @param delay the timeout delay
     * @param replyConsumer the callback for the reply
     */
    void sendComponent(Consumer<Component> componentConsumer, int delay, BiConsumer<Component, Boolean> replyConsumer);

    /**
     * Sends a {@link Component} to the server
     *
     * In this method the reply is also included and will be accepted in the given consumer
     * If no reply is given or the reply timed out the boolean part of the consumer will be set to false
     *
     * @param hytoraComponent the component
     * @param delay the timeout delay
     * @param replyConsumer the callback for the reply
     */
    void sendComponent(Component hytoraComponent, int delay, BiConsumer<Component, Boolean> replyConsumer);

    /**
     * Creates the connection
     * In case of....
     *
     *  SERVER : It will create the server and start
     *
     *  CLIENT: It will connect to the server
     *
      * @return the future
     */
    Future<HytoraConnection> createConnection();

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
                component.setRecipient("all");
            }

            packet.write(component);
        });
    }

    /**
     * The {@link PacketManager} to manage
     * all the incoming packets
     *
     * @return manager
     */
    PacketManager getPacketManager();

    /**
     * The {@link UserManager} to manage
     * all users (only used on {@link HytoraServer}
     *
     * @return manager
     */
    UserManager getUserManager();

    /**
     * Registers a {@link PacketHandler}
     *
     * @param packetHandler the handler
     */
    default void registerPacketHandler(PacketHandler packetHandler) {
        this.getPacketManager().getPacketHandlers().add(packetHandler);
    }

    /**
     * Unregisters a {@link PacketHandler}
     *
     * @param packetHandler the handler
     */
    default void unregisterPacketHandler(PacketHandler packetHandler) {
        this.getPacketManager().getPacketHandlers().remove(packetHandler);
    }
}
