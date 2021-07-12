package de.lystx.hytoracloud.bridge.bukkit.impl.listener;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
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


        Player player = event.getPlayer();
        ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getUniqueId());

        if (CloudBridge.getInstance().getProxyBridge().commandExecute(ICloudPlayer, event.getMessage())) {
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (CloudDriver.getInstance().isUseChat() && CloudDriver.getInstance().getPermissionPool().isAvailable()) {
            event.setCancelled(true);
            String message = event.getMessage();
            PermissionGroup group = CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(event.getPlayer().getUniqueId());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String chatFormat = ChatColor.translateAlternateColorCodes('&', (group.getChatFormat().trim().isEmpty() ? CloudDriver.getInstance().getChatFormat() : group.getChatFormat()));
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
