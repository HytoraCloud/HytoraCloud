package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DriverEventPlayerQuit extends DriverEventPlayer {

    private static final long serialVersionUID = 6318013271660671240L;

    public DriverEventPlayerQuit(ICloudPlayer player) {
        super(player);
    }
}
