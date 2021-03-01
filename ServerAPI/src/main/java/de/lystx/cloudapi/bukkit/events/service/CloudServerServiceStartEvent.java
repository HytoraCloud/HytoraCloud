package de.lystx.cloudapi.bukkit.events.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class CloudServerServiceStartEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final Service service;

    public CloudServerServiceStartEvent(Service service) {
        this.service = service;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
