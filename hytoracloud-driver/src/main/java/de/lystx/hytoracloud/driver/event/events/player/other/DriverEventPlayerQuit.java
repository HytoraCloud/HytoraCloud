package de.lystx.hytoracloud.driver.event.events.player.other;

import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import lombok.Getter;

@Getter
public class DriverEventPlayerQuit extends DriverEventPlayer {

    private static final long serialVersionUID = 6318013271660671240L;

    public DriverEventPlayerQuit(ICloudPlayer player) {
        super(player);
    }
}
