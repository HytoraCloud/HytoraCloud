package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class DriverEventServiceUpdate extends CloudEvent {

    private static final long serialVersionUID = -1100611233570488924L;
    private final IService service;

}
