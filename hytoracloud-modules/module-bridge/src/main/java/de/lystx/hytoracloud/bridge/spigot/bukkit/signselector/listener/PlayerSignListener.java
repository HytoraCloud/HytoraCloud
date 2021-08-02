package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.listener;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.CloudSign;
import de.lystx.hytoracloud.driver.commons.service.IService;
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
        ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());
        CloudSign cloudSign = ServerSelector.getInstance().getSignManager().getSignUpdater().getCloudSign(sign.getLocation());

        if (cloudPlayer != null && cloudSign != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(ServerSelector.getInstance().getSignManager().getSignUpdater().getServiceMap().get(cloudSign));
            if (service == null) {
                return;
            }

            if (service.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName()) && !service.getGroup().isMaintenance()) {
                player.sendMessage(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getAlreadyConnected().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix()));
                return;
            }
            cloudPlayer.connect(service);
        }
    }
}
