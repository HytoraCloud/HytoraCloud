package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventServiceUpdate extends DriverEventService {

    private static final long serialVersionUID = -1100611233570488924L;

    public DriverEventServiceUpdate(IService service) {
        super(service);
    }
}
