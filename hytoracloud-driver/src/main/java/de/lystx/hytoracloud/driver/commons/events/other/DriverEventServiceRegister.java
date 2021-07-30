package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventServiceRegister extends DriverEventService {

    private static final long serialVersionUID = 4240659919948246792L;

    public DriverEventServiceRegister(IService service) {
        super(service);
    }

}
