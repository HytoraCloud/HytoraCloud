package de.lystx.cloudapi.bukkit.listener.player;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.manager.npc.impl.PacketReader;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.result.login.ResultPacketLoginSuccess;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.NoSuchElementException;

public class PlayerJoinListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {

        //Player has joine server is not stopping
        CloudServer.getInstance().setWaitingForPlayer(false); //Disables stopping server
        if (CloudServer.getInstance().getTaskId() != -1) {
            CloudAPI.getInstance().getScheduler().cancelTask(CloudServer.getInstance().getTaskId()); //Cancelling stop ask
        }

        Player player = event.getPlayer();

        CloudConnection connection = new CloudConnection(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress());
        

        CloudAPI.getInstance().executeAsyncQuery(new ResultPacketLoginSuccess(connection, CloudAPI.getInstance().getService().getName()), document -> {
            if (!document.getDocument().getBoolean("allow", true)) {
                event.setJoinMessage(null);
                Bukkit.getScheduler().runTask(CloudServer.getInstance(), () -> player.kickPlayer(CloudAPI.getInstance().getPrefix() + "§cIt seems like you tried to connect directly to the service and not via a BungeeCord!"));
                return;
            }

            if (CloudServer.getInstance().getLabyMod() != null && CloudAPI.getInstance().getNetworkConfig().getLabyModConfig().isEnabled()) {
                if (!CloudAPI.getInstance().getNetworkConfig().getLabyModConfig().isVoiceChat()) {
                    CloudServer.getInstance().getLabyMod().disableVoiceChat(player);
                }

                CloudServer.getInstance().getLabyMod().sendCurrentPlayingGamemode(player, true, CloudAPI.getInstance().getNetworkConfig().getLabyModConfig()
                        .getServerSwitchMessage().replace("&", "§").replace("%service%", CloudAPI.getInstance().getService().getName())
                        .replace("%group%", CloudAPI.getInstance().getService().getServiceGroup().getName())
                        .replace("%online_players%", Bukkit.getOnlinePlayers().size() + " ")
                        .replace("%max_players%", Bukkit.getMaxPlayers() + " "));
            }

            int percent = CloudAPI.getInstance().getService().getServiceGroup().getNewServerPercent();
            double bukkitPercent = ((Bukkit.getMaxPlayers() / Bukkit.getOnlinePlayers().size()) * 100);
            if (percent <= 100 && bukkitPercent >= percent) {
                CloudAPI.getInstance().getNetwork().startService(CloudAPI.getInstance().getService().getServiceGroup().getName(), new SerializableDocument().append("waitingForPlayers", true));
            } else {
                player.sendMessage(percent + " - " + bukkitPercent + "%");
            }

            //NPCs injecting for InteractEvent
            if (!CloudServer.getInstance().isNewVersion()) {
                PacketReader packetReader = new PacketReader(player);
                try {
                    packetReader.inject();
                    CloudServer.PACKET_READERS.put(player.getUniqueId(), packetReader);
                    CloudServer.getInstance().getNpcManager().updateNPCS(CloudServer.getInstance().getNpcManager().getDocument(), player, true);

                    if (CloudAPI.getInstance().isNametags() && CloudAPI.getInstance().getPermissionPool().isAvailable()) {

                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            PermissionGroup group = CloudAPI.getInstance().getPermissionPool().getHighestPermissionGroup(onlinePlayer.getName());
                            if (group == null) {
                                CloudAPI.getInstance().messageCloud(CloudAPI.getInstance().getService().getName(), "§cPlayer §e" + player.getName() + " §ccouldn't update permissionGroup");
                                return;
                            }
                            CloudServer.getInstance().getNametagManager().setNametag(group.getPrefix(), group.getSuffix(), group.getId(), onlinePlayer);
                        }
                    }
                } catch (NoSuchElementException e){
                    e.printStackTrace();
                }
            }
        });
    }

}
