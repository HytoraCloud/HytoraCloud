package de.lystx.hytoracloud.module.serverselector.spigot.manager.sign;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base.CloudSign;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.layout.SignLayOut;
import de.lystx.hytoracloud.driver.utils.minecraft.ServerPinger;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class SignManager {

    private List<CloudSign> cloudSigns;
    private SignLayOut signLayOut;

    private ServerPinger serverPinger;
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
