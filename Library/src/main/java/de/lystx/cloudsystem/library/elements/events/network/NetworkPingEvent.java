package de.lystx.cloudsystem.library.elements.events.network;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import lombok.Getter;

@Getter
public class NetworkPingEvent extends Event {

    private final CloudConnection connection;

    public NetworkPingEvent(CloudConnection connection) {
        this.connection = connection;
    }
}
