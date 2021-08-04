package de.lystx.hytoracloud.driver.event.events.other;

import de.lystx.hytoracloud.driver.service.IService;
import lombok.Getter;

@Getter
public class DriverEventServiceStop extends DriverEventService {

    private static final long serialVersionUID = -1109833014123476850L;

    public DriverEventServiceStop(IService service) {
        super(service);
    }
}
