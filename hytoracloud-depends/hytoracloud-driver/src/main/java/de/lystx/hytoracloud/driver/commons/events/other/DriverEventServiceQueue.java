package de.lystx.hytoracloud.driver.commons.events.other;

import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.Getter;

@Getter
public class DriverEventServiceQueue extends DriverEventService {

    private static final long serialVersionUID = 7102337796322133111L;

    public DriverEventServiceQueue(IService service) {
        super(service);
    }
}
