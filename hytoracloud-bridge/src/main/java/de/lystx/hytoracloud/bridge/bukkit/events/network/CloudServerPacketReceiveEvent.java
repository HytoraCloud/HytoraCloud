package de.lystx.hytoracloud.bridge.bukkit.events.network;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @RequiredArgsConstructor
public class CloudServerPacketReceiveEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final HytoraPacket packet;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
