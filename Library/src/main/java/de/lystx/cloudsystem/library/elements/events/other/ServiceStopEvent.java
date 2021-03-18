package de.lystx.cloudsystem.library.elements.events.other;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ServiceStopEvent extends Event {

    private final Service service;
}
