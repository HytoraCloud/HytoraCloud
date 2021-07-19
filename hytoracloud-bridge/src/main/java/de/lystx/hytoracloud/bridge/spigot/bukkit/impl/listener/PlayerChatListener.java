package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.listener;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import utillity.PlaceHolder;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());

        String command = event.getMessage().substring(1).split(" ")[0];

        if (CloudDriver.getInstance().getInstance(CommandService.class).getCommand(command) != null) {
            event.setCancelled(true);
            CloudDriver.getInstance().getInstance(CommandService.class).execute(cloudPlayer, true, event.getMessage());
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

                String formatted = PlaceHolder.apply(chatFormat, group).replace("%message%", message).replace("%player%", event.getPlayer().getName());

                onlinePlayer.sendMessage(formatted);
            }
        }
    }

}
