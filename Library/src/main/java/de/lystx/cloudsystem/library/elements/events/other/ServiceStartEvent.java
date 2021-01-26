package de.lystx.cloudsystem.library.elements.events.other;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import lombok.Getter;

@Getter
public class ServiceStartEvent extends Event {

    private final Service service;

    public ServiceStartEvent(Service service) {
        this.service = service;
    }
}
