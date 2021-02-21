package de.lystx.cloudapi.bukkit.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.manager.npc.impl.PacketReader;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInPlayerExecuteCommand;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.packets.result.login.ResultPacketLoginSuccess;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.util.*;

public class PlayerListener implements Listener {

    private final Map<UUID, PacketReader> packetReaders;
    private final List<UUID> noJoinMessages;

    public PlayerListener() {
        this.noJoinMessages = new LinkedList<>();
        this.packetReaders = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleSyncPlayerLoginEvent(PlayerLoginEvent event) {
        if (CloudAPI.getInstance().getNetwork().getServices().isEmpty()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CloudAPI.getInstance().getPrefix() + "§cThere was a massive error! Please report it to an Administrator!");
            return;
        }
        if (!CloudAPI.getInstance().isJoinable()) {
            try {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CloudAPI.getInstance().getPrefix() + "§cThis service ist not joinable yet§c!");
            } catch (NullPointerException e) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cThere was an error§c! Try again!");
            }
        } else {
            CloudServer.getInstance().updatePermissions(event.getPlayer());
        }
    }



    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PacketReader packetReader = this.packetReaders.getOrDefault(player.getUniqueId(), new PacketReader(player));
        packetReader.uninject();
        this.packetReaders.remove(player.getUniqueId());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/stop") || event.getMessage().startsWith("/bukkit:stop")) {
            if (!event.getPlayer().hasPermission("bukkit.command.stop")) {
                event.getPlayer().sendMessage(CloudAPI.getInstance().getPrefix() +  "§cYou aren't allowed to perform this command!");
                return;
            }
            event.setCancelled(true);
            CloudServer.getInstance().shutdown();
        }
        CloudAPI.getInstance().sendPacket(new PacketPlayInPlayerExecuteCommand(event.getPlayer().getName(), event.getMessage()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {

        CloudServer.getInstance().setWaitingForPlayer(false); //Disables stopping server in 1 minute if nobody joined
        Player player = event.getPlayer();

        CloudConnection connection = new CloudConnection(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress());

        CloudAPI.getInstance().executeAsyncQuery(new ResultPacketLoginSuccess(connection, CloudAPI.getInstance().getService().getName()), document -> {
            if (!document.getDocument().getBoolean("allow", true)) {
                event.setJoinMessage(null);
                Bukkit.getScheduler().runTask(CloudServer.getInstance(), () -> player.kickPlayer(CloudAPI.getInstance().getPrefix() + "§cThere was an error! It seems like you are already on the network or tried to connect twice!"));
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
            if (percent <= 100 && ((Bukkit.getOnlinePlayers().size() / Bukkit.getMaxPlayers()) * 100) >= percent) {
                CloudAPI.getInstance().getNetwork().startService(CloudAPI.getInstance().getService().getServiceGroup().getName(), new Document().append("waitingForPlayers", true));
            }

            //NPCs injecting for InteractEvent
            if (!CloudServer.getInstance().isNewVersion()) {
                PacketReader packetReader = new PacketReader(player);
                try {
                    packetReader.inject();
                    this.packetReaders.put(player.getUniqueId(), packetReader);
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

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (CloudAPI.getInstance().isUseChat() && CloudAPI.getInstance().getPermissionPool().isAvailable()) {
            event.setCancelled(true);
            String message = event.getMessage();
            PermissionGroup group = CloudAPI.getInstance().getPermissionPool().getHighestPermissionGroup(event.getPlayer().getName());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String chatFormat = ChatColor.translateAlternateColorCodes('&', (group.getChatFormat().trim().isEmpty() ? CloudAPI.getInstance().getChatFormat() : group.getChatFormat()));
                onlinePlayer.sendMessage(chatFormat
                        .replace("%display%", group.getDisplay().replace("&", "§"))
                        .replace("%group%", group.getName().replace("&", "§"))
                        .replace("%message%", message)
                        .replace("%prefix%", group.getPrefix().replace("&", "§"))
                        .replace("%suffix%", group.getSuffix().replace("&", "§"))
                        .replace("%player%", event.getPlayer().getName())
                        .replace("%id%", group.getId() + ""));
            }
        }
    }


    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() == Material.AIR) {
            return;
        }
        if (!(event.getClickedBlock().getType().equals(Material.WALL_SIGN))) {
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        Player player = event.getPlayer();
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(player.getName());
        if (cloudPlayer == null) {
            player.sendMessage(CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getErrorMessage().replace("&", "§").replace("%error%",
                    "You couldn't be found in global CloudPlayer list! Either rejoin or notify a server Administrator or a Cloud Administrator!").replace("%prefix%", CloudAPI.getInstance().getPrefix()));
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            CloudSign cloudSign = CloudServer.getInstance().getSignManager().getSignUpdater().getCloudSign(sign.getLocation());
            if (cloudSign == null) {
                return;
            }
            Service meta = CloudServer.getInstance().getSignManager().getSignUpdater().getService(cloudSign);
            if (meta == null) {
                return;
            }
            if (meta.getName().equalsIgnoreCase(CloudAPI.getInstance().getService().getName())) {
                player.sendMessage(CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getAlreadyConnectedMessage().replace("&", "§").replace("%prefix%", CloudAPI.getInstance().getPrefix()));
                return;
            }
            cloudPlayer.sendToServer(CloudAPI.getInstance().getCloudClient(), meta.getName());
        }
    }

}
