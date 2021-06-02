package de.lystx.hytoracloud.module.serverselector.spigot.event;

import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl.NPC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @RequiredArgsConstructor
public class CloudServerNPCInteractEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final NPC npc;
    private final boolean rightclick;


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

}
