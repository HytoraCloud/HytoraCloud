package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventService extends CloudEvent implements Serializable {
    private static final long serialVersionUID = 5949887369885475643L;

    private final ServiceObject service;

    public DriverEventService(IService service) {
        this.service = (ServiceObject) service;
    }

    public IService getService() {
        return service;
    }
}
