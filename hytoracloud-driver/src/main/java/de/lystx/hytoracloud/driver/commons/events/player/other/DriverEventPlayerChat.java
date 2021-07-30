package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

 @Getter
public class DriverEventPlayerChat extends DriverEventPlayer {

    private static final long serialVersionUID = 5311219730205981453L;
    private final String message;

    public DriverEventPlayerChat(ICloudPlayer player, String message) {
        super(player);
        this.message = message;
    }
}
