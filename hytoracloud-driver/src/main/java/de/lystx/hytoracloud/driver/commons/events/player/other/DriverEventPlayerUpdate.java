package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class DriverEventPlayerUpdate extends CloudEvent implements Serializable {

    private static final long serialVersionUID = 1231855920836347843L;

    private final ICloudPlayer player;
}
