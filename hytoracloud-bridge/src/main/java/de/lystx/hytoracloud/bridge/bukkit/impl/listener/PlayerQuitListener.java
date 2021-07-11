package de.lystx.hytoracloud.bridge.bukkit.impl.listener;

import de.lystx.hytoracloud.bridge.bukkit.HytoraCloudBukkitBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerQuitListener implements Listener {


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HytoraCloudBukkitBridge.getInstance().startStopTimer();
        CloudDriver.getInstance().getThisService().update();
    }

}
