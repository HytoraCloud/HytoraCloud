package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.service.managing.event.base.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class DriverEventServiceStart extends CloudEvent {

    private final Service service;

}
