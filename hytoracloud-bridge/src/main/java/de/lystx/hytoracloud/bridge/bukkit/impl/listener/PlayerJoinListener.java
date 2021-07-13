package de.lystx.hytoracloud.bridge.bukkit.impl.listener;

import de.lystx.hytoracloud.bridge.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.bukkit.utils.CloudPermissibleBase;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handleLogin(PlayerLoginEvent event) {

        IService service = CloudDriver.getInstance().getCurrentService();
        Player player = event.getPlayer();
        ICloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());

        if (cloudPlayer == null) {
            event.setKickMessage(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getOnlyProxyJoin().replace("%prefix%", CloudDriver.getInstance().getPrefix()));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        //Player has joined; server is not stopping
        if (BukkitBridge.getInstance().getTaskId() != -1) {
            CloudDriver.getInstance().getScheduler().cancelTask(BukkitBridge.getInstance().getTaskId()); //Cancelling stop ask
        }

        CloudDriver.getInstance().getCurrentService().update();
        int percent = CloudDriver.getInstance().getCurrentService().getGroup().getNewServerPercent();

        if (percent <= 100 && (((double) Bukkit.getOnlinePlayers().size()) / (double) Bukkit.getMaxPlayers()) * 100 >= percent) {

            PropertyObject propertyObject = new PropertyObject();
            propertyObject.append("waitingForPlayers", true);

            CloudDriver.getInstance().getServiceManager().startService(service.getGroup(), propertyObject);
        }

        CloudDriver.getInstance().updatePermissions(event.getPlayer(), new CloudPermissibleBase(event.getPlayer()));

    }
}
