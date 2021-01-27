package de.lystx.cloudapi.bukkit.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.CloudServerSubChannelMessageEvent;
import de.lystx.cloudapi.bukkit.manager.npc.impl.NPC;
import de.lystx.cloudapi.bukkit.manager.npc.impl.PacketReader;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInPlayerExecuteCommand;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutForceRegisterPlayer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final Map<UUID, PacketReader> packetReaders;

    public PlayerListener() {
        this.packetReaders = new HashMap<>();
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        boolean allow = false;
        for (Integer value : CloudAPI.getInstance().getNetwork().getProxies().keySet()) {
            Service service = CloudAPI.getInstance().getNetwork().getProxy(value);
            if (service == null) {
                continue;
            }
            if (event.getRealAddress().getHostAddress().equalsIgnoreCase(service.getHost() + ":" + service.getPort()) || event.getHostname().length() > 32) {
                allow = true;
            }
        }
        if (CloudAPI.getInstance().getNetwork().getServices().isEmpty()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CloudAPI.getInstance().getPrefix() + "§cTry again in a few seconds! §8[§bServices§8]");
            return;
        }
        if (CloudAPI.getInstance().getNetwork().getServiceGroups().isEmpty()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CloudAPI.getInstance().getPrefix() + "§cTry again in a few seconds! §8[§bGroups§8]");
            return;
        }
        if (!CloudAPI.getInstance().getPermissionPool().isAvailable()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CloudAPI.getInstance().getPrefix() + "§cTry again in a few seconds! §8[§bPermissionPool§8]");
            return;
        }
        if (!allow || !CloudAPI.getInstance().isJoinable()) {
            try {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CloudAPI.getInstance().getPrefix() + "§cPlease join only via the given §eproxies§c!");
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
        //CloudAPI.getInstance().sendPacket(new PacketPlayInPlayerExecuteCommand(event.getPlayer().getName(), event.getMessage()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PacketReader packetReader = new PacketReader(player);
        try {
            packetReader.inject();
        } catch (NoSuchElementException e){}
        this.packetReaders.put(player.getUniqueId(), packetReader);
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int onlinepercent = ((onlinePlayers / Bukkit.getMaxPlayers()) * 100);

        if (CloudAPI.getInstance().getNetworkConfig().getLabyModConfig().isEnabled()) {
            if (!CloudAPI.getInstance().getNetworkConfig().getLabyModConfig().isVoiceChat()) {
                CloudServer.getInstance().getLabyMod().disableVoiceChat(player);
            }
            Service s = CloudAPI.getInstance().getService();
            CloudServer.getInstance().getLabyMod().sendCurrentPlayingGamemode(player, true, CloudAPI.getInstance().getNetworkConfig().getLabyModConfig()
                    .getServerSwitchMessage().replace("&", "§").replace("%service%", s.getName())
                    .replace("%group%", s.getServiceGroup().getName())
                    .replace("%online_players%", Bukkit.getOnlinePlayers().size() + " ")
                    .replace("%max_players%", Bukkit.getMaxPlayers() + " "));
        }
        if (onlinepercent >= onlinePlayers) {
            CloudAPI.getInstance().getNetwork().startService(CloudAPI.getInstance().getService().getServiceGroup());
        }

        CloudServer.getInstance().getNpcManager().updateNPCS(CloudServer.getInstance().getNpcManager().getDocument(), player);
        if (CloudAPI.getInstance().isNametags() && CloudAPI.getInstance().getPermissionPool().isAvailable()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                PermissionGroup group = CloudAPI.getInstance().getPermissionPool().getHighestPermissionGroup(onlinePlayer.getName());
                CloudServer.getInstance().getNametagManager().setNametag(group.getPrefix(), group.getSuffix(), group.getId(), onlinePlayer);
            }
        }
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(() -> {
            if (CloudAPI.getInstance().getCloudPlayers().get(player.getName()) == null) {
                CloudAPI.getInstance().sendPacket(new PacketPlayOutForceRegisterPlayer(player.getName()));
            }
        }, 4L);
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
