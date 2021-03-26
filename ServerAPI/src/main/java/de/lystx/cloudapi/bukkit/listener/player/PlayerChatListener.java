package de.lystx.cloudapi.bukkit.listener.player;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketInPlayerExecuteCommand;
import de.lystx.cloudsystem.library.elements.packets.result.player.ResultPacketCloudPlayer;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerChatListener implements Listener {


    @EventHandler
    public void handle(PlayerCommandPreprocessEvent event) {
        if (CloudAPI.getInstance().getCommandService().getCommand(event.getMessage().substring(1).split(" ")[0]) != null) {
            event.setCancelled(true);
            Player proxiedPlayer = event.getPlayer();
            CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(proxiedPlayer.getUniqueId());
            if (cloudPlayer == null) {
                proxiedPlayer.kickPlayer(CloudAPI.getInstance().getPrefix() + "§cYou couldn't be found in CloudPlayers! Please rejoin");
                return;
            }
            CloudAPI.getInstance().execute(() -> CloudAPI.getInstance().getCommandService().execute(cloudPlayer, true, event.getMessage()));
        }
        new PacketInPlayerExecuteCommand(event.getPlayer().getName(), event.getMessage()).unsafe().async().send(CloudAPI.getInstance());
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

}
