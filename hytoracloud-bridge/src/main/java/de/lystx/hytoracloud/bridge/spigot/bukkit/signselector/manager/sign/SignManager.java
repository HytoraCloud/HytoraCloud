package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.sign;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.CloudSign;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.SignConfiguration;
import de.lystx.hytoracloud.driver.commons.minecraft.other.ServerPinger;
import de.lystx.hytoracloud.driver.cloudservices.global.scheduler.Scheduler;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class SignManager {

    /**
     * All cloudSigns (updated by packet)
     */
    private List<CloudSign> cloudSigns;

    /**
     * The configuration (update by packet)
     */
    private SignConfiguration configuration;

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
        this.configuration = SignConfiguration.createDefault();
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
            new Thread(() -> this.signUpdater.run(), "signThread").start();
        } catch (NullPointerException e) {
            Scheduler.getInstance().scheduleDelayedTask(this::run, 5L);
        }
    }
}
