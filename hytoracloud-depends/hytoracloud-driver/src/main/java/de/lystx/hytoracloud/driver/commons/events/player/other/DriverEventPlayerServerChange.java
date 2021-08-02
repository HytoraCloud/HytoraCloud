package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
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
