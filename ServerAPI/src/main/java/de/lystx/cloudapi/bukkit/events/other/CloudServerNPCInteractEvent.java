package de.lystx.cloudapi.bukkit.events.other;

import de.lystx.cloudapi.bukkit.manager.npc.impl.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloudServerNPCInteractEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private boolean rightclick;
    private NPC npc;
    private Player player;

    public CloudServerNPCInteractEvent(Player player, NPC npc, Boolean rightclick) {
        this.npc = npc;
        this.rightclick = rightclick;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isRightclick() {
        return rightclick;
    }

    public NPC getNPC() {
        return npc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

}
