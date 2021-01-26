package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class CloudPlayerJoinEvent extends Event {

    private final CloudPlayer cloudPlayer;

    public CloudPlayerJoinEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }
}
