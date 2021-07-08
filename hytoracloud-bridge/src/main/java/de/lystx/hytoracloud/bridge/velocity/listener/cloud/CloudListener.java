package de.lystx.hytoracloud.bridge.velocity.listener.cloud;

import com.velocitypowered.api.proxy.ProxyServer;
import de.lystx.hytoracloud.bridge.velocity.HytoraCloudVelocityBridge;
import de.lystx.hytoracloud.driver.elements.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerConnection;

//TODO: CALL EVENTS
public class CloudListener implements NetworkHandler {

    private final ProxyServer proxyServer = HytoraCloudVelocityBridge.getInstance().getServer();

    @Override
    public void onServerStart(Service service) {

    }

    @Override
    public void onServerRegister(Service service) {

    }

    @Override
    public void onServerQueue(Service service) {
        NetworkHandler.super.onServerQueue(service);
    }

    @Override
    public void onServerStop(Service service) {
        NetworkHandler.super.onServerStop(service);
    }

    @Override
    public void onServerUpdate(Service service) {
        NetworkHandler.super.onServerUpdate(service);
    }

    @Override
    public void onGroupUpdate(ServiceGroup group) {
        NetworkHandler.super.onGroupUpdate(group);
    }

    @Override
    public void onPlayerJoin(CloudPlayer cloudPlayer) {
        NetworkHandler.super.onPlayerJoin(cloudPlayer);
    }

    @Override
    public void onServerChange(CloudPlayer cloudPlayer, String server) {
        NetworkHandler.super.onServerChange(cloudPlayer, server);
    }

    @Override
    public void onPlayerQuit(CloudPlayer cloudPlayer) {
        NetworkHandler.super.onPlayerQuit(cloudPlayer);
    }

    @Override
    public void onNetworkPing(PlayerConnection connection) {
        NetworkHandler.super.onNetworkPing(connection);
    }
}
