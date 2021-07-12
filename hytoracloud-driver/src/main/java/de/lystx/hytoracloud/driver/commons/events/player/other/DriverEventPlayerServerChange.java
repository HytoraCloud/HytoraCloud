package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventPlayerServerChange extends CloudEvent implements Serializable {

    private final CloudPlayer player;
    private final IService IService;

}
