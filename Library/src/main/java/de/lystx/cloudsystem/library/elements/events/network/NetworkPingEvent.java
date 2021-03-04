package de.lystx.cloudsystem.library.elements.events.network;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class NetworkPingEvent extends Event {

    private final CloudConnection connection;

}
