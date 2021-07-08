package de.lystx.hytoracloud.bridge.bukkit.listener.player;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.bridge.bukkit.HytoraCloudBukkitBridge;
import de.lystx.hytoracloud.bridge.bukkit.utils.CloudPermissibleBase;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handleLogin(PlayerLoginEvent event) {
        Service service = CloudDriver.getInstance().getThisService();

        Player player = event.getPlayer();
        CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());

        if (cloudPlayer == null) { //TODO: CHECK IF
            //TODO: ADD Message
            event.setKickMessage("%prefix% §cYou were not registered via Proxy before!".replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        //Player has joined; server is not stopping
        if (HytoraCloudBukkitBridge.getInstance().getTaskId() != -1) {
            CloudDriver.getInstance().getScheduler().cancelTask(HytoraCloudBukkitBridge.getInstance().getTaskId()); //Cancelling stop ask
        }

        CloudDriver.getInstance().getThisService().update();
        int percent = CloudDriver.getInstance().getThisService().getServiceGroup().getNewServerPercent();

        if (percent <= 100 && (((double) Bukkit.getOnlinePlayers().size()) / (double) Bukkit.getMaxPlayers()) * 100 >= percent) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("waitingForPlayers", true);

            CloudDriver.getInstance().getServiceManager().startService(service.getServiceGroup(), jsonObject);
        }

        cloudPlayer.setService(CloudDriver.getInstance().getThisService());
        cloudPlayer.update();
        CloudDriver.getInstance().updatePermissions(event.getPlayer(), new CloudPermissibleBase(event.getPlayer()));

    }
}
