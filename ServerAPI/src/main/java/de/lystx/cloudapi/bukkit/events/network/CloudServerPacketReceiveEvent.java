package de.lystx.cloudapi.bukkit.events.network;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class CloudServerPacketReceiveEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final Packet packet;

    public CloudServerPacketReceiveEvent(Packet packet) {
        this.packet = packet;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
