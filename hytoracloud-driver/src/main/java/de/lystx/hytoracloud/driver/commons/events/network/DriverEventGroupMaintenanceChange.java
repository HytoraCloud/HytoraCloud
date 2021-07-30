package de.lystx.hytoracloud.driver.commons.events.network;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceGroupObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventGroupMaintenanceChange extends CloudEvent implements Serializable {

    private static final long serialVersionUID = 408639406513078509L;

    private final ServiceGroupObject group;

    private final boolean changedTo;

    public DriverEventGroupMaintenanceChange(IServiceGroup group, boolean changedTo) {
        this.group = (ServiceGroupObject) group;
        this.changedTo = changedTo;
    }

    public IServiceGroup getGroup() {
        return group;
    }
}
