package de.lystx.hytoracloud.driver.commons.events.network;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.IEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventReload implements IEvent, Serializable {

    private static final long serialVersionUID = -7686220063541509128L;

}
