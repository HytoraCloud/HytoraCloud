package de.lystx.hytoracloud.bridge.bukkit.signselector.manager.sign;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.base.CloudSign;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.layout.SignLayOut;
import de.lystx.hytoracloud.driver.utils.minecraft.ServerPinger;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class SignManager {

    /**
     * All cloudsigns (updated by packet)
     */
    private List<CloudSign> cloudSigns;

    /**
     * The signlayout (update by packet)
     */
    private SignLayOut signLayOut;

    /**
     * The server pinger
     */
    private ServerPinger serverPinger;

    /**
     * The sign updater
     */
    private SignUpdater signUpdater;

    public SignManager() {
        this.cloudSigns = new LinkedList<>();
        this.signLayOut = new SignLayOut();
        this.serverPinger = new ServerPinger();
        this.signUpdater = new SignUpdater(this);
        this.run();
    }

    /**
     * Starts the Sign Scheduler
     */
    public void run() {
        try {
            if (!CloudDriver.getInstance().getCurrentService().getGroup().isLobby()) {
                return;
            }
            CloudDriver.getInstance().execute(() -> this.signUpdater.run());
        } catch (NullPointerException e) {
            Scheduler.getInstance().scheduleDelayedTask(this::run, 5L);
        }
    }
}
