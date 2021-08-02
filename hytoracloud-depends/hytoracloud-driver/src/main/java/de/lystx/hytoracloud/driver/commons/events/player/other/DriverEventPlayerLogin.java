package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DriverEventPlayerLogin extends DriverEventPlayer {

    private static final long serialVersionUID = 3872398608906826935L;

    /**
     * If you want to cancel something
     */
    private String targetComponent;

    public DriverEventPlayerLogin(ICloudPlayer player) {
        super(player);
    }
}
