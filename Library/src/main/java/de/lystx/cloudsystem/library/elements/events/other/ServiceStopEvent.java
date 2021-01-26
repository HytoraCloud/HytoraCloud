package de.lystx.cloudsystem.library.elements.events.other;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import lombok.Getter;

@Getter
public class ServiceStopEvent extends Event {

    private final Service service;

    public ServiceStopEvent(Service service) {
        this.service = service;
    }
}
