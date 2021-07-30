package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventServiceQueue extends DriverEventService {

    private static final long serialVersionUID = 7102337796322133111L;

    public DriverEventServiceQueue(IService service) {
        super(service);
    }
}
