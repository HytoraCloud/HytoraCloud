package de.lystx.hytoracloud.driver.commons.events.network;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventNetworkPing extends CloudEvent implements Serializable {

    private static final long serialVersionUID = -7686220063541509128L;
    private final PlayerConnection connection;

}
