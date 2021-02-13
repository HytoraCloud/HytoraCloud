package de.lystx.cloudapi.bukkit.listener;

import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.CloudServerNetworkUpdateEvent;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

import java.util.UUID;

public class CloudListener implements NetworkHandler {

    @Override
    public void onServerStart(Service service) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onServerQueue(Service service) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onServerStop(Service service) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onServerUpdate(Service service) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onGroupUpdate(ServiceGroup group) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onPlayerJoin(CloudPlayer cloudPlayer) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onServerChange(CloudPlayer cloudPlayer, String server) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onPlayerQuit(CloudPlayer cloudPlayer) {
        try {
            CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerNetworkUpdateEvent());
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onNetworkPing(UUID connectionUUID) {
    }

    @Override
    public void onDocumentReceive(String channel, String key, Document document, ServiceType type) {

    }
}
