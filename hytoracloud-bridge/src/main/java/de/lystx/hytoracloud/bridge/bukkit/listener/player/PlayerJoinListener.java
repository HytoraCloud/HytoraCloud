package de.lystx.hytoracloud.bridge.bukkit.listener.player;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.bridge.bukkit.CloudServer;
import de.lystx.hytoracloud.bridge.bukkit.events.player.CloudPlayerLabyModJoinEvent;
import de.lystx.hytoracloud.bridge.bukkit.utils.CloudPermissibleBase;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestKey;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPing;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.player.featured.labymod.LabyModPlayer;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerConnection;
import io.thunder.packet.impl.response.PacketRespond;
import io.thunder.packet.impl.response.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.function.Consumer;

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
        if (CloudServer.getInstance().getTaskId() != -1) {
            CloudDriver.getInstance().getScheduler().cancelTask(CloudServer.getInstance().getTaskId()); //Cancelling stop ask
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

    @EventHandler
    public void handleLabyMod(CloudPlayerLabyModJoinEvent event) {
        CloudPlayer cloudPlayer = event.getPlayer();
        LabyModPlayer labyModPlayer = cloudPlayer.getLabyModPlayer();

        if (CloudServer.getInstance().getLabyMod() != null && CloudDriver.getInstance().getNetworkConfig().getLabyModConfig().isEnabled()) {
            if (!CloudDriver.getInstance().getNetworkConfig().getLabyModConfig().isVoiceChat()) {
                labyModPlayer.disableVoicechat();
            }

            labyModPlayer.updateGamemode(CloudDriver.getInstance().getNetworkConfig().getLabyModConfig()
                    .getServerSwitchMessage().replace("&", "§").replace("%service%", CloudDriver.getInstance().getThisService().getName())
                    .replace("%group%", CloudDriver.getInstance().getThisService().getServiceGroup().getName())
                    .replace("%online_players%", Bukkit.getOnlinePlayers().size() + "")
                    .replace("%max_player%", Bukkit.getMaxPlayers() + " ")
                    .replace("%id%", CloudDriver.getInstance().getThisService().getServiceID() + "")
                    .replace("%receiver%", CloudDriver.getInstance().getThisService().getServiceGroup().getReceiver() + "")
                    .replace("%max_players%", Bukkit.getMaxPlayers() + ""));

        }
    }

}
