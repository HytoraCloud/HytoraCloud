package de.lystx.cloudapi.bukkit.listener.player;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerSignListener implements Listener {

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
            cloudPlayer.connect(meta.getName());
        }
    }
}
