package de.lystx.hytoracloud.driver.event.events.other;

import de.lystx.hytoracloud.driver.service.IService;
import lombok.Getter;

@Getter
public class DriverEventServiceUpdate extends DriverEventService {

    private static final long serialVersionUID = -1100611233570488924L;

    public DriverEventServiceUpdate(IService service) {
        super(service);
    }
}
