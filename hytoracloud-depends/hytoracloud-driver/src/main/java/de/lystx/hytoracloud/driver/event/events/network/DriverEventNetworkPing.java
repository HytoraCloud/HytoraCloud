package de.lystx.hytoracloud.driver.event.events.network;

import de.lystx.hytoracloud.driver.event.IEvent;
import de.lystx.hytoracloud.driver.wrapped.PlayerConnectionObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventNetworkPing implements IEvent, Serializable {

    private static final long serialVersionUID = -7686220063541509128L;
    private final PlayerConnectionObject connection;

}
