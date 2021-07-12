package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class DriverEventPlayerChat extends CloudEvent {

    private final CloudPlayer player;

    private final String message;
}
