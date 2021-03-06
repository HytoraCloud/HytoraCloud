package de.lystx.hytoracloud.module.serverselector.spigot.listener;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base.CloudSign;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
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
        ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
        if (ICloudPlayer == null) {
            player.sendMessage(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getErrorMessage().replace("&", "§").replace("%error%",
                    "You couldn't be found in global CloudPlayer list! Either rejoin or notify a server Administrator or a Cloud Administrator!").replace("%prefix%", CloudDriver.getInstance().getPrefix()));
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            CloudSign cloudSign = SpigotSelector.getInstance().getSignManager().getSignUpdater().getCloudSign(sign.getLocation());
            if (cloudSign == null) {
                return;
            }
            IService meta = SpigotSelector.getInstance().getSignManager().getSignUpdater().getService(cloudSign);
            if (meta == null) {
                return;
            }
            if (meta.getName().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                player.sendMessage(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getAlreadyConnectedMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix()));
                return;
            }
            ICloudPlayer.connect(meta);
        }
    }
}
