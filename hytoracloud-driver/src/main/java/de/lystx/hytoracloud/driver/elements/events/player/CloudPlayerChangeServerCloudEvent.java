package de.lystx.hytoracloud.driver.elements.events.player;

import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class CloudPlayerChangeServerCloudEvent extends CloudEvent implements Serializable {

    private final CloudPlayer cloudPlayer;
    private final String newServer;

}
