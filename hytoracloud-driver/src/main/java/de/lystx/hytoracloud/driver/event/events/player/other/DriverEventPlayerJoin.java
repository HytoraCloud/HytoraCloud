package de.lystx.hytoracloud.driver.event.events.player.other;

import de.lystx.hytoracloud.driver.event.ICancellable;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
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
