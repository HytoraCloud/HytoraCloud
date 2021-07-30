package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventServiceStop extends DriverEventService {

    private static final long serialVersionUID = -1109833014123476850L;

    public DriverEventServiceStop(IService service) {
        super(service);
    }
}
