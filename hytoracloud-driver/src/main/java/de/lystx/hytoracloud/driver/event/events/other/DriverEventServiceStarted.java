package de.lystx.hytoracloud.driver.event.events.other;

import de.lystx.hytoracloud.driver.service.IService;
import lombok.Getter;

@Getter
public class DriverEventServiceStarted extends DriverEventService {

    private static final long serialVersionUID = -4528963146163136026L;

    public DriverEventServiceStarted(IService service) {
        super(service);
    }
}
