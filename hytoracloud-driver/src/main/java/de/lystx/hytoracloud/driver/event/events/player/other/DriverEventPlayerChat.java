package de.lystx.hytoracloud.driver.event.events.player.other;

import de.lystx.hytoracloud.driver.player.ICloudPlayer;
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
