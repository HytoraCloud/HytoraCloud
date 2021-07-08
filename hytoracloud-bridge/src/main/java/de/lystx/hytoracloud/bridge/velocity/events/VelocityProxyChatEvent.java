package de.lystx.hytoracloud.bridge.velocity.events;

import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class VelocityProxyChatEvent {

    private final CloudPlayer player;
    private final String message;

}
