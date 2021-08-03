package de.lystx.hytoracloud.driver.event.events.player.other;

import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import lombok.Getter;

@Getter
public class DriverEventPlayerServerChange extends DriverEventPlayer {

    private static final long serialVersionUID = 9008527573162953171L;
    private final IService service;

    public DriverEventPlayerServerChange(ICloudPlayer player, IService service) {
        super(player);
        this.service = service;
    }
}
