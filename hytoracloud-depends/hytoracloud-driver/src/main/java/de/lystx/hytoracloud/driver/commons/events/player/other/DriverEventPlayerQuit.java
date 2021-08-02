package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import lombok.Getter;

@Getter
public class DriverEventPlayerQuit extends DriverEventPlayer {

    private static final long serialVersionUID = 6318013271660671240L;

    public DriverEventPlayerQuit(ICloudPlayer player) {
        super(player);
    }
}
