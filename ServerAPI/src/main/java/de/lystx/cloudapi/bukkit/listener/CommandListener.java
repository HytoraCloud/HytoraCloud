package de.lystx.cloudapi.bukkit.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent event) {

        if (CloudAPI.getInstance().getCommandService().getCommand(event.getMessage().substring(1).split(" ")[0]) != null) {
            event.setCancelled(true);
            Player proxiedPlayer = (Player)event.getPlayer();
            CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(proxiedPlayer.getUniqueId());
            if (cloudPlayer == null) {
                proxiedPlayer.kickPlayer(CloudAPI.getInstance().getPrefix() + "§cYou couldn't be found in CloudPlayers! Please rejoin");
                return;
            }
            CloudAPI.getInstance().getCommandService().execute(cloudPlayer, true, event.getMessage());
        }
    }


}
