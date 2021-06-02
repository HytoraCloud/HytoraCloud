package de.lystx.hytoracloud.bridge.bukkit.events.network;

import io.thunder.packet.Packet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @RequiredArgsConstructor
public class CloudServerPacketReceiveEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final Packet packet;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
