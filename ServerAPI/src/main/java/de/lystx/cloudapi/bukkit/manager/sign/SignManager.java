package de.lystx.cloudapi.bukkit.manager.sign;


import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import de.lystx.cloudsystem.library.service.serverselector.sign.manager.ServerPinger;
import de.lystx.cloudsystem.library.service.serverselector.sign.layout.SignLayOut;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class SignManager {

    private List<CloudSign> cloudSigns;
    private SignLayOut signLayOut;

    private ServerPinger serverPinger;
    private SignUpdater signUpdater;

    public SignManager(CloudServer cloudAPIBukkit) {
        this.cloudSigns = new LinkedList<>();
        this.signLayOut = new SignLayOut();
        this.serverPinger = new ServerPinger();
        this.signUpdater = new SignUpdater(this, cloudAPIBukkit.getCloudAPI());
        this.run();
    }


    public void run() {
        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
            return;
        }
        CloudAPI.getInstance().getScheduler().cancelTask(this.signUpdater.getAnimationScheduler());
        CloudAPI.getInstance().getExecutorService().submit(() -> this.signUpdater.run());
    }
}
