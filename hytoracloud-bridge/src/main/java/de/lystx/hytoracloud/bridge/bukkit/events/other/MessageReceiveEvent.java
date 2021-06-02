package de.lystx.hytoracloud.bridge.bukkit.events.other;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class MessageReceiveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String messageKey;
    private final JsonElement jsonMessage;



    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }
}
