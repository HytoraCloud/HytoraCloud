package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.elements.packets.both.PacketCallEvent;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public interface CloudExecutor extends Serializable {


    /**
     * Interface method for sending packet
     * @param packet
     */
    default void sendPacket(Packet packet) {
        if (packet.unsafe().isAsync()) {
            packet.unsafe().sync();
            Executors.newCachedThreadPool().execute(() -> this.sendPacket(packet));
        }
    }

    default void sendPacket(Packet packet, Consumer<PacketState> consumer) {

    }

    /**
     * Calls an Event with the {{@link de.lystx.cloudsystem.library.elements.packets.both.PacketCallEvent}}
     * @param event
     */
    default void callEvent(Event event) {
        if (event instanceof Serializable) {
            this.sendPacket(new PacketCallEvent(event).setSendBack(true));
        } else {
            System.out.println("[CloudLibrary] Couldn't call Event " + event.getClass().getSimpleName() + " because the class doesn't implement Serializable or one of the objects doesn't implement Serializable!");
        }
    }

    /**
     * Interface method for registering handler
     * @param adapter
     */
    void registerPacketHandler(Object adapter);

}
