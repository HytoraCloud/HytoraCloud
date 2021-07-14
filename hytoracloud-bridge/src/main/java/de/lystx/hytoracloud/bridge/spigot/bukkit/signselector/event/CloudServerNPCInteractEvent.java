package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.event;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl.NPC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @RequiredArgsConstructor
public class CloudServerNPCInteractEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /**
     * The player that interacted
     */
    private final Player player;

    /**
     * The npc
     */
    private final NPC npc;

    /**
     * If it was rightclicked
     */
    private final boolean rightclick;


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

}
