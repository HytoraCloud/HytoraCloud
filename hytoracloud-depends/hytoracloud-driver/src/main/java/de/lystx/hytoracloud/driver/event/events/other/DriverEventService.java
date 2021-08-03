package de.lystx.hytoracloud.driver.event.events.other;

import de.lystx.hytoracloud.driver.event.IEvent;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventService implements IEvent, Serializable {
    private static final long serialVersionUID = 5949887369885475643L;

    private final ServiceObject service;

    public DriverEventService(IService service) {
        this.service = (ServiceObject) service;
    }

    public IService getService() {
        return service;
    }
}
