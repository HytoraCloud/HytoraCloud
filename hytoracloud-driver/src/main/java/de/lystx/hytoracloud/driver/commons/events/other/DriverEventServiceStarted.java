package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventServiceStarted extends DriverEventService {

    private static final long serialVersionUID = -4528963146163136026L;

    public DriverEventServiceStarted(IService service) {
        super(service);
    }
}
