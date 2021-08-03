package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.bridge.proxy.ProxyBridge;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;


import java.util.UUID;


public class ProxyHandlerConfig implements PacketHandler {

    public void handle(Packet packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

        if (packet instanceof PacketOutGlobalInfo) {
            PacketOutGlobalInfo info = (PacketOutGlobalInfo) packet;


            //New config is maintenance -> switching
            if (info.getNetworkConfig().isMaintenance()) {
                boolean to = info.getNetworkConfig().isMaintenance();
                if (to) {
                    //switching value is off so ignoring following step

                    //maintenance is now switched on -> kicking all players if not permission

                    if (proxyBridge != null) {
                        for (String name : proxyBridge.getPlayerInfos().keySet()) {
                            UUID uniqueId = proxyBridge.getPlayerInfos().get(name);

                            if (!CloudDriver
                                    .getInstance()
                                    .getConfigManager()
                                    .getNetworkConfig()
                                    .getWhitelistedPlayers()
                                    .contains(name)
                                    && !CloudDriver.getInstance().getPermissionPool().hasPermission(uniqueId, "cloudsystem.network.maintenance")) {
                                proxyBridge.kickPlayer(uniqueId,
                                        CloudDriver.getInstance()
                                                .getConfigManager()
                                                .getNetworkConfig()
                                                .getMessageConfig()
                                                .getMaintenanceNetwork()
                                                .replace("%prefix%",
                                                        CloudDriver.getInstance().getPrefix()));
                            }
                        }
                    }

                }
            }

            if (proxyBridge == null) {
                return;
            }

            //Registering all services from the info
            for (IService service : info.getServices()) {
                if (service == null) {
                    continue;
                }
                proxyBridge.registerService(service);
            }

            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {

                //Removing all non existent services
                for (String serverInfo : CloudBridge.getInstance().getProxyBridge().getAllServices()) {

                    IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(serverInfo);
                    //Service no longer exists or is shut down
                    if (service == null) {
                        proxyBridge.removeServer(serverInfo);
                    }
                }
            }, 20L);


        }
    }

}
