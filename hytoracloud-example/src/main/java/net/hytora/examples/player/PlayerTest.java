package net.hytora.examples.player;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;

public class PlayerTest {


    public void test() {

        ICloudPlayerManager playerManager = CloudDriver.getInstance().getPlayerManager();

    }

}
