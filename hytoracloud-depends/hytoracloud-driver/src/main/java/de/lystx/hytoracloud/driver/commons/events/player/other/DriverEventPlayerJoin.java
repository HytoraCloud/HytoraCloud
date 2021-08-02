package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.ICancellable;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DriverEventPlayerJoin extends DriverEventPlayer implements ICancellable {

   private static final long serialVersionUID = 3872398608906826935L;

   private boolean cancelled;

   public DriverEventPlayerJoin(ICloudPlayer player) {
       super(player);
   }
}
