package de.lystx.cloudapi.bukkit.listener.player;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.player.CloudPlayerLabyModJoinEvent;
import de.lystx.cloudapi.bukkit.manager.npc.impl.PacketReader;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.elements.packets.result.login.ResultPacketLoginSuccess;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.NoSuchElementException;
import java.util.UUID;

public class PlayerJoinListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {

        //Player has joined; server is not stopping
        if (CloudServer.getInstance().getTaskId() != -1) {
            CloudAPI.getInstance().getScheduler().cancelTask(CloudServer.getInstance().getTaskId()); //Cancelling stop ask
        }

        final Player player = event.getPlayer();

        final CloudConnection connection = new CloudConnection(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress());

        CloudAPI.getInstance().sendPacket(new ResultPacketLoginSuccess(connection, CloudAPI.getInstance().getService().getName()));


        CloudAPI.getInstance().sendPacket(new PacketInServiceUpdate(CloudAPI.getInstance().getService()));
        int percent = CloudAPI.getInstance().getService().getServiceGroup().getNewServerPercent();

        if (percent <= 100 && (((double) Bukkit.getOnlinePlayers().size()) / (double) Bukkit.getMaxPlayers()) * 100 >= percent) {
            CloudAPI.getInstance().getNetwork().startService(CloudAPI.getInstance().getService().getServiceGroup().getName(), new SerializableDocument().append("waitingForPlayers", true));
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
                            System.out.println("[CloudAPI] Couldn't update Nametag for " + player.getName() + "! His PermissionGroup couldn't be found!");
                            return;
                        }
                        CloudServer.getInstance().getNametagManager().setNametag(group.getPrefix(), group.getSuffix(), group.getId(), onlinePlayer);
                    }
                }
            } catch (NoSuchElementException e){
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void handleLabyMod(CloudPlayerLabyModJoinEvent event) {
        CloudAPI.getInstance().execute(() -> {
            final CloudPlayer cloudPlayer = event.getPlayer();

            if (CloudServer.getInstance().getLabyMod() != null && CloudAPI.getInstance().getNetworkConfig().getLabyModConfig().isEnabled()) {
                if (!CloudAPI.getInstance().getNetworkConfig().getLabyModConfig().isVoiceChat()) {
                    cloudPlayer.getLabyModPlayer().disableVoicechat();
                }

                cloudPlayer.getLabyModPlayer().updateGamemode(CloudAPI.getInstance().getNetworkConfig().getLabyModConfig()
                        .getServerSwitchMessage().replace("&", "§").replace("%service%", CloudAPI.getInstance().getService().getName())
                        .replace("%group%", CloudAPI.getInstance().getService().getServiceGroup().getName())
                        .replace("%online_players%", Bukkit.getOnlinePlayers().size() + " ")
                        .replace("%max_player%", Bukkit.getMaxPlayers() + " ")
                        .replace("%id%", CloudAPI.getInstance().getService().getServiceID() + "")
                        .replace("%receiver%", CloudAPI.getInstance().getService().getServiceGroup().getReceiver() + "")
                        .replace("%max_players%", Bukkit.getMaxPlayers() + " "));

            }
        });
    }

}
