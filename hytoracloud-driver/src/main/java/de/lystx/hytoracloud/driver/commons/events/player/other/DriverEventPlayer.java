package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
@Getter
public class DriverEventPlayer extends CloudEvent implements Serializable {

    private static final long serialVersionUID = 6318013271660671240L;

    /**
     * The joined player
     */
    private final PlayerObject player;

    public DriverEventPlayer(ICloudPlayer player) {
        this.player = (PlayerObject) player;
    }

    public ICloudPlayer getPlayer() {
        return player;
    }
}
