package de.lystx.hytoracloud.driver.cloudservices.managing.event.base;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventMarker;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.commons.events.network.DriverEventNetworkPing;
import de.lystx.hytoracloud.driver.commons.events.other.*;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerJoin;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerQuit;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerServerChange;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;

public class DefaultListener implements EventListener {

    @EventMarker
    public void handleQueue(DriverEventServiceQueue event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerQueue(service));
    }

    @EventMarker
    public void handleStarted(DriverEventServiceStarted event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerStarted(service));
    }

    @EventMarker
    public void handleRegister(DriverEventServiceRegister event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerRegister(service));
    }

    @EventMarker
    public void handleUpdate(DriverEventServiceUpdate event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerUpdate(service));
    }

    @EventMarker
    public void handleStop(DriverEventServiceStop event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerStop(service));
    }

    @EventMarker
    public void handlePlayerJoin(DriverEventPlayerJoin event) {
        ICloudPlayer player = event.getPlayer();

        NetworkHandler.run(networkHandler -> networkHandler.onPlayerJoin(player));
    }

    @EventMarker
    public void handlePlayerQuit(DriverEventPlayerQuit event) {
        ICloudPlayer player = event.getPlayer();

        NetworkHandler.run(networkHandler -> networkHandler.onPlayerQuit(player));
    }

    @EventMarker
    public void handlePlayerServiceChange(DriverEventPlayerServerChange event) {
        ICloudPlayer player = event.getPlayer();
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerChange(player, service));
    }

    @EventMarker
    public void handleNetworkPing(DriverEventNetworkPing event) {
        PlayerConnection connection = event.getConnection();

        NetworkHandler.run(networkHandler -> networkHandler.onNetworkPing(connection));
    }

}
