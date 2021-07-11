package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.commons.service.Service;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import java.util.List;
import java.util.UUID;


public class ProxyHandlerConfig implements PacketHandler {

    public void handle(HytoraPacket packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

        if (packet instanceof PacketOutGlobalInfo) {

            PacketOutGlobalInfo info = (PacketOutGlobalInfo) packet;

            //Setting network config
            CloudDriver.getInstance().getImplementedData().put("networkConfig", info.getNetworkConfig());

            CloudDriver.getInstance().getServiceManager().setCachedServices(info.getServices());

            //New config is maintenance -> switching
            if (info.getNetworkConfig().getGlobalProxyConfig().isMaintenance()) {
                boolean to = info.getNetworkConfig().getGlobalProxyConfig().isMaintenance();
                if (!to) {
                    //switching value is off so ignoring following step
                    return;
                }
                //maintenance is now switched on -> kicking all players if not permission

                if (proxyBridge == null) {
                    return;
                }

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
                    if (service == null) {
                        continue;
                    }
                    proxyBridge.registerService(service);
                }
            }

            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {

                //Removing all non existent services
                for (String serverInfo : CloudBridge.getInstance().getProxyBridge().getAllServices()) {
                    Service service = CloudDriver.getInstance().getServiceManager().getService(serverInfo);
                    if (service == null) {
                        proxyBridge.removeServer(serverInfo);
                    }
                }
            }, 20L);


        }
    }

}
