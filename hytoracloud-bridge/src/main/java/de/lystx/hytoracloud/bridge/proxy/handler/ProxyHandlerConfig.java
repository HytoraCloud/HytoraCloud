package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.handler.PacketHandler;

import io.thunder.packet.Packet;

import java.util.List;
import java.util.UUID;


public class ProxyHandlerConfig implements PacketHandler {

    public void handle(Packet packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

        if (packet instanceof PacketOutGlobalInfo) {

            PacketOutGlobalInfo info = (PacketOutGlobalInfo) packet;

            //Setting network config
            CloudDriver.getInstance().getImplementedData().put("networkConfig", info.getNetworkConfig());

            //New config is maintenance -> switching
            if (info.getNetworkConfig().getGlobalProxyConfig().isMaintenance()) {
                boolean to = info.getNetworkConfig().getGlobalProxyConfig().isMaintenance();
                if (!to) {
                    //switching value is off so ignoring following step
                    return;
                }
                //maintenance is now switched on -> kicking all players if not permission

                for (String name : proxyBridge.getPlayerInfos().keySet()) {
                    UUID uniqueId = proxyBridge.getPlayerInfos().get(name);

                    if (!CloudDriver
                            .getInstance()
                            .getNetworkConfig()
                            .getGlobalProxyConfig()
                            .getWhitelistedPlayers()
                            .contains(name)
                            && !CloudDriver.getInstance().getPermissionPool().hasPermission(uniqueId, "cloudsystem.network.maintenance")) {
                        proxyBridge.kickPlayer(uniqueId,
                                CloudDriver.getInstance()
                                        .getNetworkConfig()
                                        .getMessageConfig()
                                        .getMaintenanceKickMessage()
                                        .replace("%prefix%",
                                                CloudDriver.getInstance().getCloudPrefix()));
                    }
                }
            }

            //Registering all services from the info
            for (List<Service> value : info.getServices().values()) {
                for (Service service : value) {
                    proxyBridge.registerService(service);
                }
            }

            //Removing all non existent services
            for (String serverInfo : CloudBridge.getInstance().getProxyBridge().getAllServices()) {
                Service service = CloudDriver.getInstance().getServiceManager().getService(serverInfo);
                if (service == null) {
                    proxyBridge.removeServer(serverInfo);
                }

            }

        }
    }

}
