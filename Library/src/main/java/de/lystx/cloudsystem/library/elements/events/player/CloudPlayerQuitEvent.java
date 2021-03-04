package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CloudPlayerQuitEvent extends Event {

    private final CloudPlayer cloudPlayer;

}
