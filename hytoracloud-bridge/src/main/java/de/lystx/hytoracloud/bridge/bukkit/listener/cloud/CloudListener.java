package de.lystx.hytoracloud.bridge.bukkit.listener.cloud;

import de.lystx.hytoracloud.bridge.bukkit.CloudServer;
import de.lystx.hytoracloud.bridge.bukkit.events.network.CloudServerNetworkUpdateEvent;
import de.lystx.hytoracloud.bridge.bukkit.events.player.CloudServerPlayerNetworkJoinEvent;
import de.lystx.hytoracloud.bridge.bukkit.events.player.CloudServerPlayerNetworkQuitEvent;
import de.lystx.hytoracloud.bridge.bukkit.events.player.CloudServerPlayerServerSwitchEvent;
//import de.lystx.bridge.bukkit.events.service.*;
import de.lystx.hytoracloud.bridge.bukkit.events.service.*;
import de.lystx.hytoracloud.driver.elements.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;

public class CloudListener implements NetworkHandler {

    @Override
    public void onServerStart(Service service) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerServiceStartEvent(service));
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onServerQueue(Service service) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerServiceQueueEvent(service));
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onServerStop(Service service) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerServiceStopEvent(service));
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onServerUpdate(Service service) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerServiceUpdateEvent(service));
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onGroupUpdate(ServiceGroup group) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerServiceGroupUpdateEvent(group));
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onPlayerJoin(CloudPlayer cloudPlayer) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerPlayerNetworkJoinEvent(cloudPlayer));
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onServerChange(CloudPlayer cloudPlayer, String server) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerPlayerServerSwitchEvent(cloudPlayer, server));
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onPlayerQuit(CloudPlayer cloudPlayer) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerPlayerNetworkQuitEvent(cloudPlayer));
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onNetworkPing(PlayerConnection connectionUUID) { }

}
