package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class CloudPlayerQuitEvent extends Event {

    private final CloudPlayer cloudPlayer;

    public CloudPlayerQuitEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }
}
