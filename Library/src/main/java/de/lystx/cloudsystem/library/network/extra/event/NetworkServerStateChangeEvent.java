package de.lystx.cloudsystem.library.network.extra.event;

import de.lystx.cloudsystem.library.service.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.cloudsystem.library.network.connection.server.NetworkServer;

/**
 * Event called when a {@link NetworkServer} changed its state
 */
@AllArgsConstructor
@Getter
public class NetworkServerStateChangeEvent extends Event {

    private final NetworkServer server;

    private final NetworkServer.State state;

}
