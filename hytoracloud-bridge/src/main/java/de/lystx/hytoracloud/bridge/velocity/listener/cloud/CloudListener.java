package de.lystx.hytoracloud.bridge.velocity.listener.cloud;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.lystx.hytoracloud.bridge.velocity.HytoraCloudVelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.service.managing.player.impl.PlayerInformation;
import net.kyori.adventure.text.Component;

public class CloudListener implements NetworkHandler {

    public void notify(int state, String servername) {

        ProxyServer server = HytoraCloudVelocityBridge.getInstance().getServer();

        for (Player player : server.getAllPlayers()) {
            if (!CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), "cloudsystem.notify")) {
                return;
            }
            PlayerInformation playerData = CloudDriver.getInstance().getPermissionPool().getPlayerInformation(player.getUniqueId());
            if (playerData != null && !playerData.isNotifyServerStart()) {
                return;
            }
            String message = null;
            switch (state){
                case 1:
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerStartMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
                    break;
                case 2:
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerStopMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
                    break;
                case 3:
                    return;

            }
            assert message != null;
            player.sendMessage(Component.text(message));
        }
    }

    @Override
    public void onServerStart(Service service) {
        this.notify(3, service.getName());
    }

    @Override
    public void onServerQueue(Service service) {
        this.notify(1, service.getName());
    }

    @Override
    public void onServerStop(Service service) {
        this.notify(2, service.getName());
    }

}
