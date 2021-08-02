package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import lombok.Getter;

@Getter
public class DriverEventPlayerUpdate extends DriverEventPlayer {

    private static final long serialVersionUID = 1231855920836347843L;

    public DriverEventPlayerUpdate(ICloudPlayer player) {
        super(player);
    }
}
