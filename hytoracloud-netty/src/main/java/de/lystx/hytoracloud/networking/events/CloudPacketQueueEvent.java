package de.lystx.hytoracloud.networking.events;

import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * The event for adding a packet to the sending queue
 */
@Getter @RequiredArgsConstructor
public class CloudPacketQueueEvent extends CloudEvent {

    private Channel channel;
    private AbstractPacket packet;
    private Consumer<AbstractPacket>[] callbacks;

    @SafeVarargs
    public CloudPacketQueueEvent(Channel channel, AbstractPacket packet, Consumer<AbstractPacket>... callbacks) {
        this.channel = channel;
        this.packet = packet;
        this.callbacks = callbacks;
    }

}
