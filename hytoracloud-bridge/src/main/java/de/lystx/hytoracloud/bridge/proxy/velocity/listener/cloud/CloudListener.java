package de.lystx.hytoracloud.bridge.proxy.velocity.listener.cloud;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.lystx.hytoracloud.bridge.proxy.velocity.VelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;
import net.kyori.adventure.text.Component;

public class CloudListener implements NetworkHandler {

    public void notify(int state, String servername) {

        ProxyServer server = VelocityBridge.getInstance().getServer();

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
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServiceStart().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getPrefix());
                    break;
                case 2:
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServiceStop().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getPrefix());
                    break;
                case 3:
                    return;

            }
            assert message != null;
            player.sendMessage(Component.text(message));
        }
    }

    @Override
    public void onServerStarted(IService service) {
        this.notify(3, service.getName());
    }

    @Override
    public void onServerQueue(IService service) {
        this.notify(1, service.getName());
    }

    @Override
    public void onServerStop(IService service) {
        this.notify(2, service.getName());
    }

}
