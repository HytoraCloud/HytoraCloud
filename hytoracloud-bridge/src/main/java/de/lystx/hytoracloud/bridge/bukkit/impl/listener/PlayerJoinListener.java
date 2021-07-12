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
        IService IService = CloudDriver.getInstance().getCurrentService();

        Player player = event.getPlayer();
        ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());

        if (ICloudPlayer == null) {
            //TODO: ADD Message
            event.setKickMessage("%prefix% §cYou were not registered via Proxy before!".replace("%prefix%", CloudDriver.getInstance().getPrefix()));
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

            PropertyObject jsonObject = new PropertyObject();
            jsonObject.append("waitingForPlayers", true);

            CloudDriver.getInstance().getServiceManager().startService(IService.getGroup(), jsonObject);
        }

        CloudDriver.getInstance().updatePermissions(event.getPlayer(), new CloudPermissibleBase(event.getPlayer()));

    }
}
