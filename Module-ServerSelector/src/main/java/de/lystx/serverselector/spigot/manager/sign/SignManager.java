package de.lystx.serverselector.spigot.manager.sign;


import de.lystx.cloudapi.CloudAPI;
import de.lystx.serverselector.cloud.manager.sign.base.CloudSign;
import de.lystx.serverselector.cloud.manager.sign.layout.SignLayOut;
import de.lystx.cloudsystem.library.service.util.ServerPinger;
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
        this.signUpdater = new SignUpdater(this, CloudAPI.getInstance());
        this.run();
    }

    /**
     * Starts the Sign Scheduler
     */
    public void run() {
        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
            return;
        }
        CloudAPI.getInstance().getExecutorService().execute(() -> this.signUpdater.run());
    }
}
