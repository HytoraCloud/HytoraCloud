package de.lystx.cloudapi.bukkit.events.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class CloudServerServiceGroupUpdateEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final ServiceGroup serviceGroup;

    public CloudServerServiceGroupUpdateEvent(ServiceGroup serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
