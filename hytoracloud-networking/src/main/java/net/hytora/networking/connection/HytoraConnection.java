package net.hytora.networking.connection;

import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.component.ComponentSender;
import net.hytora.networking.elements.component.RepliableComponent;
import net.hytora.networking.elements.packet.PacketManager;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.other.UserManager;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface HytoraConnection extends ComponentSender, Closeable {

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
     * Registers a channel handler to receive components
     *
     * @param channel the channel to listen for
     * @param consumer the consumer
     */
    void registerChannelHandler(String channel, Consumer<RepliableComponent> consumer);

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
     * The {@link PacketManager} to manage
     * all the incoming packets
     *
     * @return manager
     */
    PacketManager getPacketManager();

    /**
     * The address the connection
     * belongs to or is connected to
     * @return address
     */
    InetSocketAddress remoteAddress();

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

    /**
     * If connection is still connected
     *
     * @return boolean
     */
    boolean isAvailable();
}
