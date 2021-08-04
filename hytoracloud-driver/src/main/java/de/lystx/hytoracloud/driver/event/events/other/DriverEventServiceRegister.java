package de.lystx.hytoracloud.driver.event.events.other;

import de.lystx.hytoracloud.driver.service.IService;
import lombok.Getter;

@Getter
public class DriverEventServiceRegister extends DriverEventService {

    private static final long serialVersionUID = 4240659919948246792L;

    public DriverEventServiceRegister(IService service) {
        super(service);
    }

}
