package de.lystx.hytoracloud.driver.elements.events.network;

import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CloudNetworkPingEvent extends CloudEvent {

    private final PlayerConnection connection;

}
