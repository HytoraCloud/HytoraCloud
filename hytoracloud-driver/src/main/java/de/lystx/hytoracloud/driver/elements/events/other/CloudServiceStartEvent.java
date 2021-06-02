package de.lystx.hytoracloud.driver.elements.events.other;

import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CloudServiceStartEvent extends CloudEvent {

    private final Service service;

}
