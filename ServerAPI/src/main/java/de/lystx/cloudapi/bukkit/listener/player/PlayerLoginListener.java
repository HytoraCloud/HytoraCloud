package de.lystx.cloudapi.bukkit.listener.player;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleLogin(PlayerLoginEvent event) {
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
}
