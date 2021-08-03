package de.lystx.hytoracloud.driver.event.events.network;

import de.lystx.hytoracloud.driver.event.IEvent;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.wrapped.GroupObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventGroupMaintenanceChange implements IEvent, Serializable {

    private static final long serialVersionUID = 408639406513078509L;

    private final GroupObject group;

    private final boolean changedTo;

    public DriverEventGroupMaintenanceChange(IServiceGroup group, boolean changedTo) {
        this.group = (GroupObject) group;
        this.changedTo = changedTo;
    }

    public IServiceGroup getGroup() {
        return group;
    }
}
