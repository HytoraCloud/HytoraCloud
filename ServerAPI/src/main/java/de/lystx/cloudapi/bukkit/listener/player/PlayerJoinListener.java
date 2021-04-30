package de.lystx.cloudapi.bukkit.listener.player;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.player.CloudPlayerLabyModJoinEvent;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.elements.packets.result.login.ResultPacketLoginSuccess;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.player.featured.labymod.LabyModAddon;
import de.lystx.cloudsystem.library.service.player.featured.labymod.LabyModPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        final Service service = CloudAPI.getInstance().getService();
        final CloudConnection connection = new CloudConnection(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress());
        connection.setStart(service);

        CloudAPI.getInstance().execute(() -> {
            CloudAPI.getInstance().sendQuery(new ResultPacketLoginSuccess(connection));
            CloudAPI.getInstance().sendQuery(new ResultPacketLoginSuccess(connection));
        });

        //Player has joined; server is not stopping
        if (CloudServer.getInstance().getTaskId() != -1) {
            CloudAPI.getInstance().getScheduler().cancelTask(CloudServer.getInstance().getTaskId()); //Cancelling stop ask
        }

        CloudAPI.getInstance().sendPacket(new PacketInServiceUpdate(service));
        int percent = CloudAPI.getInstance().getService().getServiceGroup().getNewServerPercent();

        if (percent <= 100 && (((double) Bukkit.getOnlinePlayers().size()) / (double) Bukkit.getMaxPlayers()) * 100 >= percent) {
            CloudAPI.getInstance().getNetwork().startService(service.getServiceGroup().getName(), new SerializableDocument().append("waitingForPlayers", true));
        }
    }

    @EventHandler
    public void handleLabyMod(CloudPlayerLabyModJoinEvent event) {
        final CloudPlayer cloudPlayer = event.getPlayer();
        final LabyModPlayer labyModPlayer = cloudPlayer.getLabyModPlayer();

        if (CloudServer.getInstance().getLabyMod() != null && CloudAPI.getInstance().getNetworkConfig().getLabyModConfig().isEnabled()) {
            if (!CloudAPI.getInstance().getNetworkConfig().getLabyModConfig().isVoiceChat()) {
                labyModPlayer.disableVoicechat();
            }

            labyModPlayer.updateGamemode(CloudAPI.getInstance().getNetworkConfig().getLabyModConfig()
                    .getServerSwitchMessage().replace("&", "§").replace("%service%", CloudAPI.getInstance().getService().getName())
                    .replace("%group%", CloudAPI.getInstance().getService().getServiceGroup().getName())
                    .replace("%online_players%", Bukkit.getOnlinePlayers().size() + "")
                    .replace("%max_player%", Bukkit.getMaxPlayers() + " ")
                    .replace("%id%", CloudAPI.getInstance().getService().getServiceID() + "")
                    .replace("%receiver%", CloudAPI.getInstance().getService().getServiceGroup().getReceiver() + "")
                    .replace("%max_players%", Bukkit.getMaxPlayers() + ""));

        }
    }

}
