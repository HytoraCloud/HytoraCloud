package de.lystx.cloudapi.proxy.listener.network;

import de.lystx.cloudapi.proxy.events.network.ProxyServerNetworkPingEvent;
import de.lystx.cloudapi.proxy.events.player.ProxyServerPlayerNetworkJoinEvent;
import de.lystx.cloudapi.proxy.events.player.ProxyServerPlayerNetworkQuitEvent;
import de.lystx.cloudapi.proxy.events.player.ProxyServerPlayerServerSwitchEvent;
import de.lystx.cloudapi.proxy.events.service.*;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import net.md_5.bungee.api.ProxyServer;

public class CloudListener implements NetworkHandler {


    @Override
    public void onServerStart(Service service) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerServiceStartEvent(service));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerQueue(Service service) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerServiceQueueEvent(service));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerStop(Service service) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerServiceStopEvent(service));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerUpdate(Service service) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerServiceUpdateEvent(service));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGroupUpdate(ServiceGroup group) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerServiceGroupUpdateEvent(group));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerJoin(CloudPlayer cloudPlayer) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerPlayerNetworkJoinEvent(cloudPlayer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerChange(CloudPlayer cloudPlayer, String server) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerPlayerServerSwitchEvent(cloudPlayer, server));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerQuit(CloudPlayer cloudPlayer) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerPlayerNetworkQuitEvent(cloudPlayer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkPing(CloudConnection cloudConnection) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerNetworkPingEvent(cloudConnection));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDocumentReceive(String channel, String key, Document document, ServiceType type) {

    }

}
