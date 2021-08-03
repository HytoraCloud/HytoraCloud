package de.lystx.hytoracloud.driver.event.events.other;

import de.lystx.hytoracloud.driver.service.IService;
import lombok.Getter;

@Getter
public class DriverEventServiceQueue extends DriverEventService {

    private static final long serialVersionUID = 7102337796322133111L;

    public DriverEventServiceQueue(IService service) {
        super(service);
    }
}
