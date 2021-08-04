package de.lystx.hytoracloud.driver.event.events.player.other;

import de.lystx.hytoracloud.driver.event.IEvent;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.wrapped.PlayerObject;
import lombok.Getter;

import java.io.Serializable;
@Getter
public class DriverEventPlayer implements IEvent, Serializable {

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
