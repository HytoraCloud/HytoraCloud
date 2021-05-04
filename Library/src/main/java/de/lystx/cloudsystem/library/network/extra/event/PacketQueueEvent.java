package de.lystx.cloudsystem.library.network.extra.event;

import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.service.event.Event;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * The event for adding a packet to the sending queue
 */
@Getter @RequiredArgsConstructor
public class PacketQueueEvent extends Event {

    private Channel channel;
    private AbstractPacket packet;
    private Consumer<AbstractPacket>[] callbacks;

    @SafeVarargs
    public PacketQueueEvent(Channel channel, AbstractPacket packet, Consumer<AbstractPacket>... callbacks) {
        this.channel = channel;
        this.packet = packet;
        this.callbacks = callbacks;
    }

}
