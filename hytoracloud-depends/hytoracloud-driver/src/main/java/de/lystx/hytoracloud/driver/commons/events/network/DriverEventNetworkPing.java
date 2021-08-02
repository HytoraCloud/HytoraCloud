package de.lystx.hytoracloud.driver.commons.events.network;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.IEvent;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerConnectionObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventNetworkPing implements IEvent, Serializable {

    private static final long serialVersionUID = -7686220063541509128L;
    private final PlayerConnectionObject connection;

}
